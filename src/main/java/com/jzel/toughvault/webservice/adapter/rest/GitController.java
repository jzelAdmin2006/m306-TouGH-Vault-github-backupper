package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.integration.service.GitService;
import lombok.AllArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/git")
@AllArgsConstructor
public class GitController {

  // TODO remove this controller used for development purposes
  private final GitService gitService;

  @PutMapping("/clone")
  public ResponseEntity<Void> cloneRepo() throws GitAPIException {
    gitService.cloneRepository();
    return ResponseEntity.ok().build();
  }
}
