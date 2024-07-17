package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import com.example.digitalsignatureapi.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class KeyPairService {

    @Autowired
    private KeyPairRepository keyPairRepository;

    @Value("${encryption.password}")
    private String encryptionPassword;

    public KeyPair generateKeyPair(String userId) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        java.security.KeyPair keyPair = keyGen.genKeyPair();

        KeyPair keyPairEntity = new KeyPair();
        keyPairEntity.setUserId(userId);
        keyPairEntity.setPublicKey(keyPair.getPublic().getEncoded());

        try {
            String encryptedPrivateKey = EncryptionUtil.encrypt(
                    new String(keyPair.getPrivate().getEncoded(), StandardCharsets.UTF_8),
                    encryptionPassword
            );
            keyPairEntity.setPrivateKey(encryptedPrivateKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting private key", e);
        }

        return keyPairRepository.save(keyPairEntity);
    }

    public Optional<KeyPair> getKeyPair(String userId) {
        return keyPairRepository.findByUserId(userId).map(keyPairEntity -> {
            try {
                String decryptedPrivateKey = EncryptionUtil.decrypt(
                        new String(keyPairEntity.getPrivateKey(), StandardCharsets.UTF_8),
                        encryptionPassword
                );
                keyPairEntity.setPrivateKey(decryptedPrivateKey.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Error decrypting private key", e);
            }
            return keyPairEntity;
        });
    }
}
