package com.jzel.toughvault.persistence.domain.repo;

import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repo")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE, force = true)
public class RepoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final int id;

  private final String name;
  private final String volumeLocation;
  private final Date latestPush;
  private final Date latestFetch;
}
