package com.jzel.toughvault.persistence.domain.auth;

import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE, force = true)
public class AuthEntity {

  @Id
  private final int id;

  private final String accessToken;
}
