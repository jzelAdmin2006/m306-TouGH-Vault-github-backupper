package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.business.domain.github.Auth;
import com.jzel.toughvault.integration.service.GitHubService;
import com.jzel.toughvault.integration.service.RegistrationService;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

  private final RegistrationService gitHubRegistrationService;
  private final GitHubService gitHubService;
  private final Auth auth;

  @GetMapping("/callback")
  public ResponseEntity<Void> callback(@RequestParam String code, @AuthenticationPrincipal Jwt principal)
      throws IOException {
    final String token = gitHubRegistrationService.getTokenFromCode(code);
    return principal.getClaimAsString("email").equals(gitHubService.getPrimaryEmail(token)) ? proceed(token)
        : ResponseEntity.status(403).build();
  }

  private ResponseEntity<Void> proceed(String token) {
    auth.setAccessToken(token);
    return ResponseEntity.ok().build();
  }
}
