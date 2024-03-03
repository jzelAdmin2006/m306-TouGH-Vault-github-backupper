package com.jzel.toughvault.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@ConfigurationProperties(prefix = "security")
@Data
@EnableWebSecurity
public class SecurityConfig {

  private String corsAllowedOrigins;

  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.cors(withDefaults())
        .authorizeHttpRequests(
            registry -> registry.requestMatchers("/actuator/health/**").permitAll()
                .anyRequest().authenticated()
        ).oauth2ResourceServer(oauth2Configurator -> oauth2Configurator.jwt(
            jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwt -> {
              final Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
              return new JwtAuthenticationToken(jwt, realmAccess.get("roles").stream()
                  .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                  .toList());
            })));
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(corsAllowedOrigins.split(",")));
    configuration.setAllowedMethods(List.of("GET"));
    configuration.setAllowedHeaders(List.of("content-type", "x-requested-with", "authorization"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
