package com.jzel.toughvault.business.service;

import static com.jzel.toughvault.common.config.Scheduling.MINUTES_SCAN_INTERVAL;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.business.domain.Settings;
import com.jzel.toughvault.integration.service.GitHubService;
import com.jzel.toughvault.integration.service.GitService;
import com.jzel.toughvault.persistence.domain.repo.RepoRepository;
import java.time.LocalDateTime;
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
  private final SettingsService settingsService;

  @Scheduled(cron = "0 0/" + MINUTES_SCAN_INTERVAL + " * * * *")
  public void scanForGitHubChanges() {
    updateAllRepoEntries(gitHubService.getCurrentRepositories());
  }

  public List<Repo> getAllRepoEntries() {
    return repoRepository.findAll();
  }

  public void updateAllRepoEntries(List<Repo> repos) {
    final List<Repo> existingRepos = getAllRepoEntries();
    final Set<String> repoNames = repos.stream().map(Repo::getName).collect(toSet());
    List<Repo> updatedRepos = getReposToSave(repos, existingRepos, repoNames);
    repoRepository.saveAll(updatedRepos);
    updateBackupsWhereNecessary(getAllRepoEntries());
    handleGitHubDeletedRepos(existingRepos, repoNames);
  }

  public Optional<Repo> findRepoEntryById(int id) {
    return repoRepository.findById(id);
  }

  public void backupRepo(Repo repo) {
    backupExecutor.submit(() -> {
      if (repo.getLatestFetch().get().isEmpty()) {
        gitService.cloneRepository(repo);
      } else {
        gitService.updateRepository(repo);
      }
      repo.getLatestFetch().set(repo.getLatestPush().get());
      repoRepository.save(repo);
    });
  }

  public void delete(Repo repo) {
    gitService.deleteRepository(repo);
    repoRepository.delete(repo);
  }

  public void unprotect(Repo repo) {
    gitService.deleteRepository(repo);
    repo.getLatestFetch().set(Optional.empty());
    repoRepository.save(repo);
  }

  public void restoreRepo(Repo repo) {
    backupExecutor.submit(() -> {
      gitHubService.initialiseRepo(repo);
      gitService.restoreRepo(repo);
      final Optional<LocalDateTime> restorePush = gitHubService.getRepoByName(repo.getName()).getLatestPush().get();
      restorePush.orElseThrow();
      repo.getLatestPush().set(restorePush);
      repo.getLatestFetch().set(restorePush);
      repoRepository.save(repo);
    });
  }

  private void updateBackupsWhereNecessary(List<Repo> updatedRepos) {
    final Settings settings = settingsService.getSettings();
    if (settings.isAutoRepoUpdate()) {
      updatedRepos.stream().filter(this::backupCanBeInitiated)
          .forEach(this::backupRepo); // TODO only backup new repos (alternately change setting description)
    }
    if (settings.isAutoCommitUpdate()) {
      updatedRepos.stream().filter(this::backupCanBeUpdated).forEach(this::backupRepo);
    }
  }

  private void handleGitHubDeletedRepos(List<Repo> existingRepos, Set<String> repoNames) {
    Map<Boolean, List<Repo>> deletedRepos = getGHDeletedReposByRescueSuccess(existingRepos, repoNames);
    repoRepository.deleteAll(
        deletedRepos.get(false)); // TODO (optional feature) send email notification for permanently lost repos
    repoRepository.saveAll(deletedRepos.get(true).stream().peek(r -> r.getLatestPush().set(Optional.empty()))
        .toList()); // TODO (optional feature) send email notification for new rescued repos
  }

  private List<Repo> getReposToSave(List<Repo> repos, List<Repo> existingRepos, Set<String> repoNames) {
    return concat(reposToUpdate(repos, existingRepos, repoNames), newRepos(repos, existingRepos)).toList();
  }

  private Stream<Repo> reposToUpdate(List<Repo> repos, List<Repo> existingRepos, Set<String> repoNames) {
    Map<String, Repo> reposFromGHByName = repos.stream().collect(toMap(Repo::getName, identity()));
    return existingRepos.stream()
        .filter(existingRepo -> repoNames.contains(existingRepo.getName()))
        .map(existingRepo -> getUpdatedRepo(existingRepo, reposFromGHByName));
  }

  private Repo getUpdatedRepo(Repo existingRepo, Map<String, Repo> reposByName) {
    final Repo currentRepoFromGitHub = reposByName.get(existingRepo.getName());
    return new Repo(
        existingRepo.getId(),
        existingRepo.getName(),
        currentRepoFromGitHub.getVolumeLocation(),
        currentRepoFromGitHub.isPrivate(),
        currentRepoFromGitHub.getLatestPush(),
        existingRepo.getLatestFetch()
    );
  }

  private boolean backupCanBeInitiated(Repo repo) {
    return repo.getLatestFetch().get().isEmpty() && repo.getLatestPush().get().isPresent();
  }

  private boolean backupCanBeUpdated(Repo repo) {
    return repo.getLatestPush().get()
        .map(lp -> lp.isAfter(repo.getLatestFetch().get().orElse(lp)))
        .orElse(false);
  }

  private Stream<Repo> newRepos(List<Repo> repos, List<Repo> existingRepos) {
    return repos.stream()
        .filter(
            repo -> existingRepos.stream().noneMatch(existingRepo -> existingRepo.getName().equals(repo.getName())));
  }

  private Map<Boolean, List<Repo>> getGHDeletedReposByRescueSuccess(List<Repo> existingRepos, Set<String> repoNames) {
    return existingRepos.stream()
        .filter(existingRepo -> !repoNames.contains(existingRepo.getName()))
        .collect(partitioningBy(r -> r.getLatestFetch().get().isPresent()));
  }
}
