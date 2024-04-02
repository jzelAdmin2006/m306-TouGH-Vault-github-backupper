package com.jzel.toughvault.integration.service;

import static java.util.Objects.requireNonNull;
import static okhttp3.MediaType.get;

import com.google.gson.JsonObject;
import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.business.domain.github.Auth;
import com.jzel.toughvault.config.SshConfig;
import com.jzel.toughvault.integration.adapter.model.GitHubEmailDto;
import com.jzel.toughvault.integration.adapter.model.GitHubGraphQLDto;
import com.jzel.toughvault.integration.adapter.model.GitHubGraphQLDto.RepositoryNodeDto;
import com.jzel.toughvault.integration.adapter.model.GitHubUserDto;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GitHubService {

  public static final Type GITHUB_EMAILS_ARRAY_TYPE = new TypeToken<List<GitHubEmailDto>>() {
  }.getType();
  private static final String USER_URL = "https://api.github.com/user";
  private static final String EMAILS_URL = USER_URL + "/emails";
  private static final String KEYS_URL = USER_URL + "/keys";
  private static final String INSTALLATIONS_URL = USER_URL + "/installations";
  private static final String REPOS_URL = USER_URL + "/repos";

  private static final MediaType JSON = get("application/json; charset=utf-8");
  private final Auth auth;
  private final Gson gson;
  private final OkHttpClient client;
  private final IntegrationMapperService mapperService;
  private final ExecutorService sshKeyExchangeExecutor;
  private final SshConfig ssh;

  public String getPrimaryEmail(String token) throws IOException {
    try (Response response = client.newCall(addAuthorization(token, new Builder().url(EMAILS_URL)
        .get())
        .build()).execute()) {
      return response.isSuccessful() ? extractPrimaryEmail(response) : throwUnexpectedCodeException(response);
    }
  }

  public boolean tokenRefersToInstallation(String token) throws IOException {
    try (Response response = client.newCall(
        addAuthorization(token, new Builder().url(INSTALLATIONS_URL)
            .get())
            .build()).execute()) {
      return requireNonNull(response.body()).string().contains("\"%s\"".formatted(getUserName(() -> token)));
    }
  }

  @SneakyThrows
  public List<Repo> getCurrentRepositories() {
    final String userName = getUserName();
    GitHubGraphQLDto gitHubResponse = gson.fromJson(executeGraphQlQuery(getQuery(userName, null)),
        GitHubGraphQLDto.class);
    List<RepositoryNodeDto> nodes = new ArrayList<>(
        gitHubResponse.getData().getUser().getRepositoriesContributedTo().getNodes());
    while (gitHubResponse.getData().getUser().getRepositoriesContributedTo().getPageInfo().isHasNextPage()) {
      gitHubResponse = gson.fromJson(
          executeGraphQlQuery(
              getQuery(userName, gitHubResponse.getData().getUser().getRepositoriesContributedTo().getPageInfo()
                  .getEndCursor())), GitHubGraphQLDto.class);
      nodes.addAll(gitHubResponse.getData().getUser().getRepositoriesContributedTo().getNodes());
    }
    return nodes.stream().filter(Objects::nonNull).map(mapperService::fromDto).toList();
  }

  public void exchangeSshKey() {
    sshKeyExchangeExecutor.submit(this::executeSshKeyExchange);
  }

  @SneakyThrows
  public void initialiseRepo(Repo repo) {
    try (Response response = client.newCall(
        addAuthorization(auth.getAccessToken().orElseThrow(), new Builder().url(REPOS_URL)
            .post(RequestBody.create(createRepoInitJson(repo).toString(), JSON))).build()).execute()) {
      if (!response.isSuccessful()) {
        throwUnexpectedCodeException(response);
      }
    }
  }

  @SneakyThrows(IOException.class)
  private void executeSshKeyExchange() {
    final String token = auth.getAccessToken().orElseThrow();
    try (Response getResponse = client.newCall(
        addAuthorization(token, new Builder().url(KEYS_URL)
            .get()).build()).execute()) {
      if (getResponse.isSuccessful()) {
        if (!containsPublicSshKey(getResponse)) {
          try (Response postResponse = client.newCall(
              addAuthorization(token, new Builder().url(KEYS_URL)
                  .post(RequestBody.create(createSshJson().toString(), JSON))).build()).execute()) {
            if (!postResponse.isSuccessful()) {
              throwUnexpectedCodeException(postResponse);
            }
          }
        }
      } else {
        throwUnexpectedCodeException(getResponse);
      }
    }
  }

  private boolean containsPublicSshKey(Response getResponse) throws IOException {
    return requireNonNull(getResponse.body()).string().contains(ssh.getPublicKey());
  }

  private Builder addAuthorization(String token, Builder builder) {
    return builder.addHeader("Authorization", "Bearer " + token);
  }

  private String extractPrimaryEmail(Response response) throws IOException {
    List<GitHubEmailDto> emailList = gson.fromJson(requireNonNull(response.body()).string(),
        GITHUB_EMAILS_ARRAY_TYPE);
    return emailList.stream().filter(GitHubEmailDto::isPrimary).findFirst().orElseThrow().getEmail();
  }

  private String getUserName() throws IOException {
    return getUserName(() -> auth.getAccessToken().orElseThrow());
  }

  private String getUserName(Supplier<String> tokenSup) throws IOException {
    try (Response response = client.newCall(
        addAuthorization(tokenSup.get(), new Builder().url(USER_URL)
            .get())
            .build()).execute()) {
      return response.isSuccessful() ?
          gson.fromJson(requireNonNull(response.body()).string(), GitHubUserDto.class).getLogin()
          : throwUnexpectedCodeException(response);
    }
  }

  private String executeGraphQlQuery(String query) throws IOException {
    try (Response response = client.newCall(addAuthorization(new Builder()
        .url("https://api.github.com/graphql")
        .post(RequestBody.create(query, JSON))).build()).execute()) {
      return response.isSuccessful() ?
          requireNonNull(response.body()).string()
          : throwUnexpectedCodeException(response);
    }
  }

  private String throwUnexpectedCodeException(Response response) throws IOException {
    throw new IOException("Unexpected code " + response);
  }

  private Builder addAuthorization(Builder builder) {
    return builder.addHeader("Authorization", "Bearer " + auth.getAccessToken().orElseThrow());
  }

  private String getQuery(@NotNull String login, @Nullable String cursor) {
    final String query = """
        query getContributions($login: String!, $contributionTypes: [RepositoryContributionType], $afterCursor: String) {
          user(login: $login) {
            repositoriesContributedTo(contributionTypes: $contributionTypes, first: 100, after: $afterCursor, includeUserRepositories: true, orderBy: {field: PUSHED_AT, direction: DESC}) {
              pageInfo {
                hasNextPage
                endCursor
              }
              nodes {
                nameWithOwner
                pushedAt
                isPrivate
              }
            }
          }
        }
        """;
    return """
        {
          "query": "%s",
          "variables": {
            "login": "%s",
            "contributionTypes": ["COMMIT", "REPOSITORY"],
            "afterCursor": %s
          }
        }
        """.formatted(query.replace("\n", "\\n").replace("\"", "\\\""), login,
        cursor == null ? "null" : "\"" + cursor + "\"");
  }

  private JsonObject createSshJson() {
    JsonObject sshKey = new JsonObject();
    sshKey.addProperty("title", "TouGH-Vault SSH");
    sshKey.addProperty("key", ssh.getPublicKey());
    return sshKey;
  }

  private JsonObject createRepoInitJson(Repo repo) {
    JsonObject initRepo = new JsonObject();
    initRepo.addProperty("name", repo.name().split("/")[1]);
    initRepo.addProperty("private", repo.isPrivate());
    return initRepo;
  }
}
