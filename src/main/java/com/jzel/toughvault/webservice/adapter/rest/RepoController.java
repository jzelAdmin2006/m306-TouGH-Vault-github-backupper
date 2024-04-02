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
import org.springframework.web.bind.annotation.PutMapping;
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

  @PutMapping("/backup/{id}")
  public ResponseEntity<Void> protect(@PathVariable(name = "id") int id) {
    return repoService.findRepoEntryById(id).map(this::proceedBackup).orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/backup")
  public ResponseEntity<Void> protectAll() {
    return repoService.getAllRepoEntries().stream().map(this::proceedBackup)
        .filter(entity -> entity.getStatusCode().isError()).findFirst()
        .orElse(ResponseEntity.accepted().build());
  }

  @PostMapping("/backup/{id}")
  public ResponseEntity<Void> restore(@PathVariable(name = "id") int id) {
    return repoService.findRepoEntryById(id).map(this::proceedRestoration).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/backup/{id}")
  public ResponseEntity<Void> unprotect(@PathVariable(name = "id") int id) {
    return repoService.findRepoEntryById(id).map(this::proceedDeleteBackup).orElse(ResponseEntity.notFound().build());
  }

  private ResponseEntity<Void> proceedDeletion(Repo repo) {
    repoService.delete(repo);
    return ResponseEntity.ok().build();
  }

  private ResponseEntity<Void> proceedDeleteBackup(Repo repo) {
    repoService.unprotect(repo);
    return ResponseEntity.ok().build();
  }

  private ResponseEntity<Void> proceedBackup(Repo repo) {
    repoService.backupRepo(repo);
    return ResponseEntity.accepted().build();
  }

  private ResponseEntity<Void> proceedRestoration(Repo repo) {
    repoService.restoreRepo(repo);
    return ResponseEntity.accepted().build();
  }
}
