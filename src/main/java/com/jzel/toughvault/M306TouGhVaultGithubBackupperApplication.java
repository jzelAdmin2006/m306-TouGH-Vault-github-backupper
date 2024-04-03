package com.jzel.toughvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class M306TouGhVaultGithubBackupperApplication {

  public static void main(String[] args) {
    SpringApplication.run(M306TouGhVaultGithubBackupperApplication.class, args);
  }

}
