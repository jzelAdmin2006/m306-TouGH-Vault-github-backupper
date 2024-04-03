package com.jzel.toughvault.integration.service;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.parse;
import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.integration.adapter.model.GitHubGraphQLDto.RepositoryNodeDto;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;

@Service
public class IntegrationMapperService {

  public Repo fromDto(final RepositoryNodeDto repoNodeDto) {
    return new Repo(0, repoNodeDto.getNameWithOwner(), encode(repoNodeDto.getNameWithOwner(), UTF_8),
        repoNodeDto.isPrivate(), new AtomicReference<>(toBusinessDate(repoNodeDto.getPushedAt())),
        new AtomicReference<>(Optional.empty()));
  }

  private Optional<LocalDateTime> toBusinessDate(String pushedDate) {
    return Optional.of(LocalDateTime.ofInstant(parse(pushedDate), systemDefault()));
  }
}
