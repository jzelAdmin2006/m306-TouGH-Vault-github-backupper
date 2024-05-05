package com.jzel.toughvault.business.domain;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Repo {

  private final int id;
  private String name;
  private String volumeLocation;
  private final boolean isPrivate;
  private final AtomicReference<Optional<LocalDateTime>> latestPush;
  private final AtomicReference<Optional<LocalDateTime>> latestFetch;

}
