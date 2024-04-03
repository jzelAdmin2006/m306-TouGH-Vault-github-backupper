package com.jzel.toughvault.persistence.domain.settings;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsPersistence extends JpaRepository<SettingsEntity, Integer> {

  @NotNull
  @Cacheable("settings")
  List<SettingsEntity> findAll();

  @NotNull
  @Override
  @CacheEvict(value = "settings", allEntries = true)
  <S extends SettingsEntity> S save(@NotNull S settingsEntity);
}
