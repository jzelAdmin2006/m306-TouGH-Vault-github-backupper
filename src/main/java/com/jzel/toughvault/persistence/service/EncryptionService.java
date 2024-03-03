package com.jzel.toughvault.persistence.service;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

  private final StrongTextEncryptor textEncryptor;

  public EncryptionService(@Value("${encryption.secret}") String secretKey) {
    textEncryptor = new StrongTextEncryptor();
    textEncryptor.setPassword(secretKey);
  }

  public String encrypt(String data) {
    return textEncryptor.encrypt(data);
  }

  public String decrypt(String encryptedData) {
    return textEncryptor.decrypt(encryptedData);
  }

}
