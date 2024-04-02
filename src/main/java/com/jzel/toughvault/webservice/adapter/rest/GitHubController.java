package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.business.service.RepoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
@AllArgsConstructor
public class GitHubController {

  private final RepoService repoService;

  @PutMapping("/scan")
  public ResponseEntity<Void> scanForGitHubChanges() {
    repoService.scanForGitHubChanges();
    return ResponseEntity.ok().build();
  }
}
