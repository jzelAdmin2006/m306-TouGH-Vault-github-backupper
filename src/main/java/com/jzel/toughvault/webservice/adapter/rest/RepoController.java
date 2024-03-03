package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.business.domain.github.Auth;
import com.jzel.toughvault.business.service.RepoService;
import com.jzel.toughvault.webservice.adapter.model.RepoDto;
import com.jzel.toughvault.webservice.service.WebMapperService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repo")
@AllArgsConstructor
public class RepoController {

  private final RepoService repoService;
  private final WebMapperService webMapperService;
  private final Auth auth;

  @GetMapping()
  public ResponseEntity<List<RepoDto>> getRepos() {
    if (auth.getAccessToken().isPresent()) {
      return ResponseEntity.ok(repoService.getAllRepoEntries().stream().map(webMapperService::toDto).toList());
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}