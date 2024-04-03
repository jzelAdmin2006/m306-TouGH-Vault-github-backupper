package com.jzel.toughvault.webservice.service;

import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.business.domain.Settings;
import com.jzel.toughvault.webservice.adapter.model.RepoDto;
import com.jzel.toughvault.webservice.adapter.model.ScanInfoDto;
import com.jzel.toughvault.webservice.adapter.model.SettingsDto;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class WebMapperService {

  private static final Function<Optional<LocalDateTime>, Date> OPTIONAL_DATE_TO_DTO_DATE =
      o -> o.map(WebMapperService::toDate).orElse(null);

  private static Date toDate(LocalDateTime lastScanTime) {
    return Date.from(lastScanTime.atZone(systemDefault()).toInstant());
  }

  public RepoDto toDto(final Repo repo) {
    return new RepoDto(repo.getId(), repo.getName(), repo.getVolumeLocation(),
        repo.isPrivate(),
        OPTIONAL_DATE_TO_DTO_DATE.apply(repo.getLatestPush().get()),
        OPTIONAL_DATE_TO_DTO_DATE.apply(repo.getLatestFetch().get()));
  }

  public ScanInfoDto toDto(final LocalDateTime lastScanTime, final LocalDateTime scanAllowedAt,
      final boolean scanAllowed) {
    return new ScanInfoDto(toDate(lastScanTime), toDate(scanAllowedAt), scanAllowed);
  }

  public SettingsDto toDto(final Settings settings) {
    return new SettingsDto(settings.getId(), settings.isAutoRepoUpdate(), settings.isAutoCommitUpdate());
  }
}
