package com.jzel.toughvault.integration.adapter.model;

import java.util.List;
import lombok.Data;

@Data
public class GitHubGraphQLDto {

  private DataDto data;

  @Data
  public static class DataDto {

    private UserDto user;
  }

  @Data
  public static class UserDto {

    private RepositoriesContributedToDto repositoriesContributedTo;
  }

  @Data
  public static class RepositoriesContributedToDto {

    private PageInfoDto pageInfo;
    private List<RepositoryNodeDto> nodes;
  }

  @Data
  public static class PageInfoDto {

    private boolean hasNextPage;
    private String endCursor;
  }

  @Data
  public static class RepositoryNodeDto {

    private String nameWithOwner;
    private String pushedAt;
  }
}