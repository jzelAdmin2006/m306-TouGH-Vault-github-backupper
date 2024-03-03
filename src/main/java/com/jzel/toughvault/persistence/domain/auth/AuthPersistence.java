package com.jzel.toughvault.persistence.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthPersistence extends JpaRepository<AuthEntity, Integer> {

}
