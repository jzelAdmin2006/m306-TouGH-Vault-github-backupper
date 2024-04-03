package com.jzel.toughvault.persistence.domain.settings;

import com.jzel.toughvault.business.domain.Settings;
import com.jzel.toughvault.persistence.service.PersistenceMapperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsRepository {

  private final SettingsPersistence settingsPersistence;
  private final PersistenceMapperService persistenceMapperService;

  public Settings find() {
    return persistenceMapperService.fromEntity(settingsPersistence.findAll().getFirst());
  }

  public Settings save(Settings settings) {
    return persistenceMapperService.fromEntity(settingsPersistence.save(persistenceMapperService.toEntity(settings)));
  }
}
