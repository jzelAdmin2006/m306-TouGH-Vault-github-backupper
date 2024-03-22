package com.jzel.toughvault.integration.service;

import com.jzel.toughvault.config.SshConfig;
import java.io.File;
import lombok.AllArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GitService {

  private final SshConfig ssh;

  public void cloneRepository() throws GitAPIException {
    Git.cloneRepository()
        .setURI("git@github.com:jzelAdmin2006/jzel.git") // TODO make this dynamic
        .setDirectory(new File("C:\\workspace\\jzel")) // TODO make this dynamic
        .setTransportConfigCallback(ssh.getTransportConfigCallback())
        .call();
  }
}
