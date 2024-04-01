package com.jzel.toughvault.integration.service;

import static java.util.Base64.getEncoder;
import static java.util.Objects.requireNonNull;

import com.jzel.toughvault.common.config.GitHubConfig;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import lombok.AllArgsConstructor;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

  private static final String TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token";

  private final GitHubConfig gitHubConfig;
  private final OkHttpClient client;
  private final ExecutorService postRegisterFetchExecutor;
  private final GitHubService gitHubService;

  public String getTokenFromCode(String code) {
    try (Response response = client.newCall(new Request.Builder().url(TOKEN_ENDPOINT)
        .post(new FormBody.Builder().add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", gitHubConfig.getRedirect())
            .add("scope", "user repo")
            .build())
        .addHeader("Authorization", "Basic " + getEncoder()
            .encodeToString((gitHubConfig.getClientId() + ":" + gitHubConfig.getClientSecret()).getBytes()))
        .build()).execute()) {
      if (response.isSuccessful()) {
        return requireNonNull(
            HttpUrl.parse(TOKEN_ENDPOINT + "?" + requireNonNull(response.body()).string())).queryParameter(
            "access_token");
      } else {
        throw new RuntimeException("Failed to fetch tokens: " + requireNonNull(response.body()).string());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void executeBatchPostRegisterFetch() {
    postRegisterFetchExecutor.submit(gitHubService::scanForGitHubChanges);
  }
}
