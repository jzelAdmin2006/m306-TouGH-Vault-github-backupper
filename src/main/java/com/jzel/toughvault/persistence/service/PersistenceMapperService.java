package com.jzel.toughvault.persistence.service;

import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.persistence.domain.auth.AuthEntity;
import com.jzel.toughvault.persistence.domain.repo.RepoEntity;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersistenceMapperService {

  private static final Function<Optional<LocalDateTime>, Date> OPTIONAL_DATE_TO_ENTITY_DATE =
      o -> o.map(d -> Date.from(d.atZone(systemDefault()).toInstant())).orElse(null);
  private final EncryptionService encryptionService;

  public Repo fromEntity(final RepoEntity entity) {
    return new Repo(entity.getId(), entity.getName(), entity.getVolumeLocation(),
        toLocalDateTime(entity.getLatestPush()),
        toLocalDateTime(entity.getLatestFetch()));
  }

  public RepoEntity toEntity(final Repo repo) {
    return new RepoEntity(repo.id(), repo.name(), repo.volumeLocation(),
        OPTIONAL_DATE_TO_ENTITY_DATE.apply(repo.latestPush()),
        OPTIONAL_DATE_TO_ENTITY_DATE.apply(repo.latestFetch()));
  }

  public AuthEntity toEntity(final String token) {
    return new AuthEntity(1, encryptionService.encrypt(token));
  }

  public String tokenFromEntity(final AuthEntity entity) {
    return encryptionService.decrypt(entity.getAccessToken());
  }

  private Optional<LocalDateTime> toLocalDateTime(@Nullable Date entity) {
    return Optional.ofNullable(entity).map(p -> p.toInstant().atZone(systemDefault()).toLocalDateTime());
  }
}
