package com.jzel.toughvault.business.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.persistence.domain.repo.RepoRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RepoService {

  private final RepoRepository repoRepository;

  public List<Repo> getAllRepoEntries() {
    return repoRepository.findAll();
  }

  public void updateAllRepoEntries(List<Repo> repos) {
    final List<Repo> existingRepos = getAllRepoEntries();
    final Set<String> repoNames = repos.stream().map(Repo::name).collect(toSet());
    repoRepository.saveAll(getReposToSave(repos, existingRepos, repoNames));
    repoRepository.deleteAll(getReposToDelete(existingRepos, repoNames));
  }

  private List<Repo> getReposToSave(List<Repo> repos, List<Repo> existingRepos, Set<String> repoNames) {
    return concat(reposToUpdate(repos, existingRepos, repoNames), newRepos(repos, existingRepos)).toList();
  }

  private Stream<Repo> reposToUpdate(List<Repo> repos, List<Repo> existingRepos, Set<String> repoNames) {
    Map<String, Repo> reposByName = repos.stream().collect(toMap(Repo::name, identity()));
    return existingRepos.stream()
        .filter(existingRepo -> repoNames.contains(existingRepo.name()))
        .map(existingRepo -> {
          final Repo updatedRepo = reposByName.get(existingRepo.name());
          return new Repo(
              existingRepo.id(),
              existingRepo.name(),
              updatedRepo.volumeLocation(),
              updatedRepo.latestPush(),
              updatedRepo.latestFetch()
          );
        });
  }

  private Stream<Repo> newRepos(List<Repo> repos, List<Repo> existingRepos) {
    return repos.stream()
        .filter(repo -> existingRepos.stream().noneMatch(existingRepo -> existingRepo.name().equals(repo.name())));
  }

  private List<Repo> getReposToDelete(List<Repo> existingRepos, Set<String> repoNames) {
    return existingRepos.stream()
        .filter(existingRepo -> !repoNames.contains(existingRepo.name()))
        .toList();
  }
}
