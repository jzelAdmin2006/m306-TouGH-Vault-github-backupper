package com.jzel.toughvault.business.domain;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Data;

@Data
public class Repo {

  private final int id;
  private final String name;
  private final String volumeLocation;
  private final boolean isPrivate;
  private final AtomicReference<Optional<LocalDateTime>> latestPush;
  private final AtomicReference<Optional<LocalDateTime>> latestFetch;

}
