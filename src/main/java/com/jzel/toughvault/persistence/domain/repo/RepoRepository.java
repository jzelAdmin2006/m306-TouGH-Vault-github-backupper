package com.jzel.toughvault.persistence.domain.repo;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.persistence.service.PersistenceMapperService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RepoRepository {

  private final RepoPersistence repoPersistence;
  private final PersistenceMapperService persistenceMapperService;

  public List<Repo> findAll() {
    return repoPersistence.findAll().stream().map(persistenceMapperService::fromEntity).toList();
  }

  public void deleteAll(List<Repo> reposToDelete) {
    repoPersistence.deleteAll(reposToDelete.stream().map(persistenceMapperService::toEntity).toList());
  }

  public void save(Repo repo) {
    repoPersistence.save(persistenceMapperService.toEntity(repo));
  }

  public void saveAll(List<Repo> repos) {
    repoPersistence.saveAll(repos.stream().map(persistenceMapperService::toEntity).toList());
  }

  public Optional<Repo> findById(int id) {
    return repoPersistence.findById(id).map(persistenceMapperService::fromEntity);
  }
}
