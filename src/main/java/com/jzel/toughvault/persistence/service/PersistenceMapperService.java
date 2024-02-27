package com.jzel.toughvault.persistence.service;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.persistence.domain.repo.RepoEntity;
import org.springframework.stereotype.Service;

@Service
public class PersistenceMapperService {

  public Repo fromEntity(final RepoEntity entity) {
    return new Repo(entity.getId(), entity.getName(), entity.getVolumeLocation(), entity.getLatestCommit());
  }
}
