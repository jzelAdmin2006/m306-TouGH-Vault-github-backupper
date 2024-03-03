package com.jzel.toughvault.webservice.service;

import static java.sql.Date.valueOf;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.webservice.adapter.model.RepoDto;
import org.springframework.stereotype.Service;

@Service
public class WebMapperService {

  public RepoDto toDto(final Repo repo) {
    return new RepoDto(repo.id(), repo.name(), repo.volumeLocation(), valueOf(repo.latestPush().toLocalDate()));
  }
}
