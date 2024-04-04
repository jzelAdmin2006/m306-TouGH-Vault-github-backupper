package com.jzel.toughvault.persistence.domain.auth;

import com.jzel.toughvault.persistence.service.PersistenceMapperService;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthRepository {

  private final AuthPersistence authPersistence;
  private final PersistenceMapperService mapperService;

  public void save(String token) {
    authPersistence.save(mapperService.toEntity(token));
  }

  public Optional<String> loadToken() {
    return authPersistence.findById(1).map(mapperService::tokenFromEntity);
  }

  public void delete() {
    authPersistence.deleteAll();
  }
}
