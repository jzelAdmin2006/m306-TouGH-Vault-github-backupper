package com.jzel.toughvault.business.domain;

import java.time.LocalDateTime;
import java.util.Optional;

public record Repo(int id, String name, String volumeLocation, boolean isPrivate, Optional<LocalDateTime> latestPush,
                   Optional<LocalDateTime> latestFetch) {

}
