package com.jzel.toughvault.business.service;

import static com.jzel.toughvault.common.config.Scheduling.MINUTES_SCAN_INTERVAL;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.integration.service.GitHubService;
import com.jzel.toughvault.integration.service.GitService;
import com.jzel.toughvault.persistence.domain.repo.RepoRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RepoService {

  private final RepoRepository repoRepository;
  private final ExecutorService backupExecutor;
  private final GitService gitService;
  private final GitHubService gitHubService;

  @Scheduled(cron = "0 0/" + MINUTES_SCAN_INTERVAL + " * * * *")
  public void scanForGitHubChanges() {
    updateAllRepoEntries(gitHubService.getCurrentRepositories());
  }

  public List<Repo> getAllRepoEntries() {
    return repoRepository.findAll();
  }

  public void updateAllRepoEntries(List<Repo> repos) {
    final List<Repo> existingRepos = getAllRepoEntries();
    final Set<String> repoNames = repos.stream().map(Repo::name).collect(toSet());
    repoRepository.saveAll(getReposToSave(repos, existingRepos, repoNames));
    repoRepository.deleteAll(getReposToDelete(existingRepos, repoNames));
  }

  public Optional<Repo> findRepoEntryById(int id) {
    return repoRepository.findById(id);
  }

  public void backupRepo(Repo repo) {
    backupExecutor.submit(() -> {
      if (repo.latestFetch().isEmpty()) {
        gitService.cloneRepository(repo);
      } else {
        gitService.updateRepository(repo);
      }
      repoRepository.save(
          new Repo(repo.id(), repo.name(), repo.volumeLocation(), repo.isPrivate(), repo.latestPush(),
              repo.latestPush()));
    });
  }

  public void delete(Repo repo) {
    gitService.deleteRepository(repo);
    repoRepository.delete(repo);
  }

  public void unprotect(Repo repo) {
    gitService.deleteRepository(repo);
    repoRepository.save(
        new Repo(repo.id(), repo.name(), repo.volumeLocation(), repo.isPrivate(), repo.latestPush(), Optional.empty()));
  }

  public void restoreRepo(Repo repo) {
    backupExecutor.submit(() -> {
      gitHubService.initialiseRepo(repo);
      gitService.restoreRepo(repo);
      repoRepository.save(
          new Repo(repo.id(), repo.name(), repo.volumeLocation(), repo.isPrivate(), repo.latestFetch(),
              repo.latestFetch()));
    });
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
              updatedRepo.isPrivate(),
              updatedRepo.latestPush(),
              existingRepo.latestFetch()
          );
        });
  }

  private Stream<Repo> newRepos(List<Repo> repos, List<Repo> existingRepos) {
    return repos.stream()
        .filter(
            repo -> existingRepos.stream().noneMatch(existingRepo -> existingRepo.name().equals(repo.name())));
  }

  private List<Repo> getReposToDelete(List<Repo> existingRepos, Set<String> repoNames) {
    return existingRepos.stream()
        .filter(existingRepo -> !repoNames.contains(existingRepo.name()))
        .toList();
  }
}
