package com.jzel.toughvault.integration.service;

import static org.eclipse.jgit.api.ListBranchCommand.ListMode.ALL;
import static org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE;
import static org.eclipse.jgit.util.FileUtils.RECURSIVE;
import static org.eclipse.jgit.util.FileUtils.delete;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.config.BackupVolumeConfig;
import com.jzel.toughvault.config.SshConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GitService {

  private final SshConfig ssh;
  private final BackupVolumeConfig volume;

  @SneakyThrows(GitAPIException.class)
  public void cloneRepository(Repo repo) {
    try (Git git = Git.cloneRepository()
        .setURI(toSshUri(repo.getName()))
        .setDirectory(volumeLocationAsDir(repo.getVolumeLocation()))
        .setTransportConfigCallback(ssh.getTransportConfigCallback())
        .setNoCheckout(true)
        .call()) {
      git.fetch().setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"))
          .setTransportConfigCallback(ssh.getTransportConfigCallback()).call();
      List<Ref> localBranches = git.branchList().call();
      for (Ref remoteRef : git.branchList().setListMode(REMOTE).call()) {
        String branchName = remoteRef.getName().substring(remoteRef.getName().lastIndexOf('/') + 1);
        if (localBranches.stream().noneMatch(ref -> ref.getName().endsWith("/" + branchName))) {
          git.branchCreate().setName(branchName).setStartPoint("origin/" + branchName).call();
        }
      }
    }
  }

  public void updateRepository(Repo repo) {
    deleteRepository(repo);
    cloneRepository(repo);
  }

  @SneakyThrows({IOException.class})
  public void deleteRepository(Repo repo) {
    File volLocAsDir = volumeLocationAsDir(repo.getVolumeLocation());
    if (volLocAsDir.exists()) {
      delete(volLocAsDir, RECURSIVE);
    }
  }

  @SneakyThrows({IOException.class, GitAPIException.class})
  public void restoreRepo(Repo repo) {
    try (Git git = Git.open(volumeLocationAsDir(repo.getVolumeLocation()))) {
      git.branchList().setListMode(ALL).call().forEach(ref -> pushBranch(ref, git));
    }
  }

  @SneakyThrows(GitAPIException.class)
  private void pushBranch(Ref ref, Git git) {
    git.push().setTransportConfigCallback(ssh.getTransportConfigCallback())
        .add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1)).call();
  }

  private String toSshUri(String repoName) {
    return "git@github.com:" + repoName + ".git";
  }

  private File volumeLocationAsDir(String repoVolumeLocation) {
    return Path.of(volume.getRoot()).resolve(repoVolumeLocation).toFile();
  }
}
