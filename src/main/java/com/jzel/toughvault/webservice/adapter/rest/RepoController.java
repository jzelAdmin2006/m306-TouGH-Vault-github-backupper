package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.business.domain.github.Auth;
import com.jzel.toughvault.business.service.RepoService;
import com.jzel.toughvault.webservice.adapter.model.RepoDto;
import com.jzel.toughvault.webservice.service.WebMapperService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    return auth.getAccessToken()
        .map(token -> ResponseEntity.ok(repoService.getAllRepoEntries().stream().map(webMapperService::toDto).toList()))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRepo(@PathVariable(name = "id") int id) {
    return repoService.findRepoEntryById(id).map(this::proceedDeletion).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/{id}/restore")
  public ResponseEntity<Void> restore(@PathVariable(name = "id") int id) {
    return repoService.findRepoEntryById(id).map(this::proceedRestoration).orElse(ResponseEntity.notFound().build());
  }


  private ResponseEntity<Void> proceedDeletion(Repo repo) {
    repoService.delete(repo);
    return ResponseEntity.ok().build();
  }

  private ResponseEntity<Void> proceedRestoration(Repo repo) {
    repoService.restoreRepo(repo);
    return ResponseEntity.accepted().build();
  }
}
