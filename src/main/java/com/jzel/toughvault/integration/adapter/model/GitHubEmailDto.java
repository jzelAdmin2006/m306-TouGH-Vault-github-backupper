package com.jzel.toughvault.integration.adapter.model;

import lombok.Getter;

@Getter
public class GitHubEmailDto {

  private String email;
  private boolean primary;
  private boolean verified;
  private String visibility;
}
