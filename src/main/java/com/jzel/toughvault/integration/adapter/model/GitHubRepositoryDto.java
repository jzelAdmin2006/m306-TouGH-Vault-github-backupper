package com.jzel.toughvault.integration.adapter.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class GitHubRepositoryDto {

  @SerializedName("full_name")
  private String fullName;

  @SerializedName("private")
  private boolean isPrivate;

  @SerializedName("pushed_at")
  private String pushedAt;
}
