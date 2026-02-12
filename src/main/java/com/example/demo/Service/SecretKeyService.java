package com.example.demo.Service;

import org.springframework.stereotype.Service;

import com.example.demo.Entities.SecrateKeysEntity;
import com.example.demo.Repository.SecrateKeysRepo;



@Service
public class SecretKeyService {

    private final SecrateKeysRepo secretKeyRepository;

    public SecretKeyService(SecrateKeysRepo secretKeyRepository) {
        this.secretKeyRepository = secretKeyRepository;
    }

    public String getValueByKeyName(String keyName) {
        return secretKeyRepository.findByKeyName(keyName)
                .map(SecrateKeysEntity ::getKeyValue)
                .orElseThrow(() ->
                        new RuntimeException("Secret key not found: " + keyName)
                );
    }

    public SecrateKeysEntity saveKey(String keyName, String keyValue) {
    	SecrateKeysEntity key = new SecrateKeysEntity();
        key.setKeyName(keyName);
        key.setKeyValue(keyValue);
        return secretKeyRepository.save(key);
    }
}
