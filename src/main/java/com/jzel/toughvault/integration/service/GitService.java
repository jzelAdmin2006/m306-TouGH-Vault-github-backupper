package com.jzel.toughvault.integration.service;

import static org.eclipse.jgit.util.FileUtils.RECURSIVE;
import static org.eclipse.jgit.util.FileUtils.delete;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.config.BackupVolumeConfig;
import com.jzel.toughvault.config.SshConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

// TODO do not checkout master branch, make sure every branch is gotten from remote
@Service
@AllArgsConstructor
public class GitService {

  private final SshConfig ssh;
  private final BackupVolumeConfig volume;

  @SneakyThrows(GitAPIException.class)
  public void cloneRepository(Repo repo) {
    Git.cloneRepository()
        .setURI(toSshUri(repo.name()))
        .setDirectory(volumeLocationAsDir(repo.volumeLocation()))
        .setTransportConfigCallback(ssh.getTransportConfigCallback())
        .call().close();
  }

  @SneakyThrows({IOException.class, GitAPIException.class})
  public void pullRepository(Repo repo) {
    try (Git git = Git.open(volumeLocationAsDir(repo.volumeLocation()))) {
      git.pull().setTransportConfigCallback(ssh.getTransportConfigCallback()).call();
    }
  }

  @SneakyThrows({IOException.class})
  public void deleteRepository(Repo repo) {
    delete(volumeLocationAsDir(repo.volumeLocation()), RECURSIVE);
  }

  @SneakyThrows({IOException.class, GitAPIException.class})
  public void restoreRepo(Repo repo) {
    try (Git git = Git.open(volumeLocationAsDir(repo.volumeLocation()))) {
      git.push().setTransportConfigCallback(ssh.getTransportConfigCallback()).call();
    }
  }

  private String toSshUri(String repoName) {
    return "git@github.com:" + repoName + ".git";
  }

  private File volumeLocationAsDir(String repoVolumeLocation) {
    return Path.of(volume.getRoot()).resolve(repoVolumeLocation).toFile();
  }
}
