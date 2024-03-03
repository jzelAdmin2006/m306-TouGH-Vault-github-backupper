package com.jzel.toughvault.business.domain.github;

import java.util.Optional;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Auth {

  private Optional<String> accessToken = Optional.empty();

  public void setAccessToken(@NotNull final String accessToken) {
    this.accessToken = Optional.of(accessToken);
  }

}
