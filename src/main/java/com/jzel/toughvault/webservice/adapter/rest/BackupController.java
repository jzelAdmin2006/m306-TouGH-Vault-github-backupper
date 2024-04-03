package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.business.service.RepoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backup")
@AllArgsConstructor
public class BackupController {

  private final RepoService repoService;


  @PutMapping("/{repoId}")
  public ResponseEntity<Void> protect(@PathVariable(name = "repoId") int id) {
    return repoService.findRepoEntryById(id).map(this::proceedBackup).orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/all")
  public ResponseEntity<Void> protectAll() {
    return repoService.getAllRepoEntries().stream().map(this::proceedBackup)
        .filter(entity -> entity.getStatusCode().isError()).findFirst()
        .orElse(ResponseEntity.accepted().build());
  }

  @DeleteMapping("/{repoId}")
  public ResponseEntity<Void> unprotect(@PathVariable(name = "repoId") int id) {
    return repoService.findRepoEntryById(id).map(this::proceedDeleteBackup).orElse(ResponseEntity.notFound().build());
  }

  private ResponseEntity<Void> proceedDeleteBackup(Repo repo) {
    repoService.unprotect(repo);
    return ResponseEntity.ok().build();
  }

  private ResponseEntity<Void> proceedBackup(Repo repo) {
    repoService.backupRepo(repo);
    return ResponseEntity.accepted().build();
  }
}
