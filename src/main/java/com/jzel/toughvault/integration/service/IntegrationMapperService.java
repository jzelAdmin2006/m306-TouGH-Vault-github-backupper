package com.jzel.toughvault.integration.service;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.parse;
import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.integration.adapter.model.GitHubGraphQLDto.RepositoryNodeDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class IntegrationMapperService {

  public Repo fromDto(final RepositoryNodeDto repoNodeDto) {
    return new Repo(0, repoNodeDto.getNameWithOwner(), encode(repoNodeDto.getNameWithOwner(), UTF_8),
        LocalDateTime.ofInstant(parse(repoNodeDto.getPushedAt()), systemDefault()));
  }
}
