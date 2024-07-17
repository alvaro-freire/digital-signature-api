package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.model.KeyPairEntity;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class KeyPairService {

    @Autowired
    private KeyPairRepository keyPairRepository;

    public KeyPairEntity generateKeyPair(String userId) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.genKeyPair();

        KeyPairEntity keyPairEntity = new KeyPairEntity();
        keyPairEntity.setUserId(userId);
        keyPairEntity.setPublicKey(keyPair.getPublic().getEncoded());
        keyPairEntity.setPrivateKey(keyPair.getPrivate().getEncoded());

        return keyPairRepository.save(keyPairEntity);
    }

    public Optional<KeyPairEntity> getKeyPair(String userId) {
        return keyPairRepository.findByUserId(userId);
    }
}
