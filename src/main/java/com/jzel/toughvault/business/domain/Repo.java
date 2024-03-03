package com.jzel.toughvault.business.domain;

import java.time.LocalDateTime;

public record Repo(int id, String name, String volumeLocation, LocalDateTime latestPush) {

}
