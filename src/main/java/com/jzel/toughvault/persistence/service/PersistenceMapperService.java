package com.jzel.toughvault.persistence.service;

import static java.sql.Date.valueOf;
import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.persistence.domain.auth.AuthEntity;
import com.jzel.toughvault.persistence.domain.repo.RepoEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersistenceMapperService {

  private final EncryptionService encryptionService;

  public Repo fromEntity(final RepoEntity entity) {
    return new Repo(entity.getId(), entity.getName(), entity.getVolumeLocation(),
        entity.getLatestPush().toInstant().atZone(systemDefault()).toLocalDateTime());
  }

  public RepoEntity toEntity(final Repo repo) {
    return new RepoEntity(repo.id(), repo.name(), repo.volumeLocation(), valueOf(repo.latestPush().toLocalDate()));
  }

  public AuthEntity toEntity(String token) {
    return new AuthEntity(1, encryptionService.encrypt(token));
  }

  public String tokenFromEntity(final AuthEntity entity) {
    return encryptionService.decrypt(entity.getAccessToken());
  }
}
