package com.jzel.toughvault.business.service;

import com.jzel.toughvault.business.domain.Settings;
import com.jzel.toughvault.persistence.domain.settings.SettingsRepository;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsService {

  private final SettingsRepository settingsRepository;

  public Settings getSettings() {
    return settingsRepository.find();
  }

  public Settings toggleAutoRepoUpdate() {
    return toggleSetting(Settings::isAutoRepoUpdate, Settings::setAutoRepoUpdate);
  }

  public Settings toggleAutoCommitUpdate() {
    return toggleSetting(Settings::isAutoCommitUpdate, Settings::setAutoCommitUpdate);
  }

  private Settings toggleSetting(Function<Settings, Boolean> getter, BiConsumer<Settings, Boolean> setter) {
    Settings settings = getSettings();
    setter.accept(settings, !getter.apply(settings));
    return settingsRepository.save(settings);
  }
}
