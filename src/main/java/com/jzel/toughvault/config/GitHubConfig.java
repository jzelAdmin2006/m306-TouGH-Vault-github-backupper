package com.jzel.toughvault.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "github")
@Data
public class GitHubConfig {

  private String clientId;
  private String clientSecret;
  private String redirect;
}
