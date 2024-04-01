package com.jzel.toughvault.config;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.concurrent.ExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutionConfig {

  @Bean
  ExecutorService sshKeyExchangeExecutor() {
    return newSingleThreadExecutor();
  }

  @Bean
  ExecutorService backupExecutor() {
    return newSingleThreadExecutor();
  }

  @Bean
  ExecutorService postRegisterFetchExecutor() {
    return newSingleThreadExecutor();
  }
}
