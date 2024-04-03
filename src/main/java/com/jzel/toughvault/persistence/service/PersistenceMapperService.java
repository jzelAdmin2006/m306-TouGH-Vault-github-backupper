package com.jzel.toughvault.persistence.service;

import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.business.domain.Settings;
import com.jzel.toughvault.persistence.domain.auth.AuthEntity;
import com.jzel.toughvault.persistence.domain.repo.RepoEntity;
import com.jzel.toughvault.persistence.domain.settings.SettingsEntity;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersistenceMapperService {

  private final EncryptionService encryptionService;

  public Repo fromEntity(final RepoEntity entity) {
    return new Repo(entity.getId(), entity.getName(), entity.getVolumeLocation(),
        entity.isPrivate(),
        toBusinessDateTime(entity.getLatestPush()),
        toBusinessDateTime(entity.getLatestFetch()));
  }

  public RepoEntity toEntity(final Repo repo) {
    return new RepoEntity(repo.getId(), repo.getName(), repo.getVolumeLocation(),
        repo.isPrivate(),
        toEntityDate(repo.getLatestPush()),
        toEntityDate(repo.getLatestFetch()));
  }

  public AuthEntity toEntity(final String token) {
    return new AuthEntity(1, encryptionService.encrypt(token));
  }

  public String tokenFromEntity(final AuthEntity entity) {
    return encryptionService.decrypt(entity.getAccessToken());
  }

  public Settings fromEntity(final SettingsEntity entity) {
    return new Settings(entity.getId(), entity.isAutoRepoUpdate(), entity.isAutoCommitUpdate());
  }

  public SettingsEntity toEntity(final Settings settings) {
    return new SettingsEntity(settings.getId(), settings.isAutoRepoUpdate(), settings.isAutoCommitUpdate());
  }

  @Nullable
  private Date toEntityDate(AtomicReference<Optional<LocalDateTime>> businessDateTime) {
    return businessDateTime.get().map(d -> Date.from(d.atZone(systemDefault()).toInstant())).orElse(null);
  }

  private AtomicReference<Optional<LocalDateTime>> toBusinessDateTime(@Nullable Date entity) {
    return new AtomicReference<>(
        Optional.ofNullable(entity).map(p -> p.toInstant().atZone(systemDefault()).toLocalDateTime()));
  }
}
