package com.jzel.toughvault.persistence.domain.settings;

import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settings")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE, force = true)
public class SettingsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final int id;

  private final boolean autoRepoUpdate;
  private final boolean autoCommitUpdate;
}
