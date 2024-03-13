package com.jzel.toughvault.business.domain.github;

import com.jzel.toughvault.persistence.domain.auth.AuthRepository;
import java.util.Optional;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Auth {

  private final AuthRepository authRepository;
  private Optional<String> accessToken;

  public Auth(AuthRepository authRepository) {
    this.authRepository = authRepository;
    this.accessToken = authRepository.loadToken().or(Optional::empty);
  }

  public void setAccessToken(@NotNull final String accessToken) {
    this.accessToken = Optional.of(accessToken);
    authRepository.save(accessToken);
  }
}
