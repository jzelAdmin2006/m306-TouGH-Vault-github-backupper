package com.jzel.toughvault.business.service;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.persistence.domain.repo.RepoRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RepoService {

  private final RepoRepository repoRepository;

  public List<Repo> getAllRepoEntries() {
    return repoRepository.findAll();
  }
}
