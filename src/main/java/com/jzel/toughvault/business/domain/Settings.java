package com.jzel.toughvault.business.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor(access = PRIVATE)
public class Settings {

  private final int id;
  private boolean autoRepoUpdate;
  private boolean autoCommitUpdate;
}
