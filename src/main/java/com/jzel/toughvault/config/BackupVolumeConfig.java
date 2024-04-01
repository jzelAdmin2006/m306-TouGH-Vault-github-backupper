package com.jzel.toughvault.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "backup.volume")
@Data
public class BackupVolumeConfig {

  private String root;
}
