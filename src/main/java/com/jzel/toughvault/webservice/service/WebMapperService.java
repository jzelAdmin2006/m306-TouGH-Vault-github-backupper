package com.jzel.toughvault.webservice.service;

import static java.time.ZoneId.systemDefault;

import com.jzel.toughvault.business.domain.Repo;
import com.jzel.toughvault.webservice.adapter.model.RepoDto;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class WebMapperService {

  private static final Function<Optional<LocalDateTime>, Date> OPTIONAL_DATE_TO_DTO_DATE =
      o -> o.map(d -> Date.from(d.atZone(systemDefault()).toInstant())).orElse(null);

  public RepoDto toDto(final Repo repo) {
    return new RepoDto(repo.id(), repo.name(), repo.volumeLocation(),
        OPTIONAL_DATE_TO_DTO_DATE.apply(repo.latestPush()),
        OPTIONAL_DATE_TO_DTO_DATE.apply(repo.latestFetch()));
  }
}
