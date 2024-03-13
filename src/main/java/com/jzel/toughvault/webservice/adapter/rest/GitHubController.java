package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.integration.service.GitHubService;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
@AllArgsConstructor
public class GitHubController {

  private final GitHubService gitHubService;

  @PutMapping("/scan")
  public ResponseEntity<Void> scanForGitHubChanges() throws IOException {
    gitHubService.scanForGitHubChanges();
    return ResponseEntity.ok().build();
  }
}
