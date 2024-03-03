package com.jzel.toughvault.common.config;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {

  @Bean
  public Gson gsonBean() {
    return new GsonBuilder()
        .setPrettyPrinting()
        .create();
  }
}
