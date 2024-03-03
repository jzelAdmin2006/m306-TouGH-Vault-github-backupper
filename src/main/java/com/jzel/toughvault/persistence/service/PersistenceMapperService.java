package com.jzel.toughvault.persistence.service;

import static java.sql.Date.valueOf;
import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.persistence.domain.repo.RepoEntity;
import org.springframework.stereotype.Service;

@Service
public class PersistenceMapperService {

  public Repo fromEntity(final RepoEntity entity) {
    return new Repo(entity.getId(), entity.getName(), entity.getVolumeLocation(),
        entity.getLatestPush().toInstant().atZone(systemDefault()).toLocalDateTime());
  }

  public RepoEntity toEntity(final Repo repo) {
    return new RepoEntity(repo.id(), repo.name(), repo.volumeLocation(), valueOf(repo.latestPush().toLocalDate()));
  }
}
