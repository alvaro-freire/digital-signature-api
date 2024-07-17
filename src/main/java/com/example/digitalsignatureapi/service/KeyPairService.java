package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.exception.UserAlreadyHasKeyPairException;
import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import com.example.digitalsignatureapi.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class KeyPairService {

    private static final Logger logger = LoggerFactory.getLogger(KeyPairService.class);

    @Autowired
    private KeyPairRepository keyPairRepository;

    @Value("${encryption.password}")
    private String encryptionPassword;

    public KeyPair generateKeyPair(String userId) throws NoSuchAlgorithmException {
        Optional<KeyPair> existingKeyPair = keyPairRepository.findByUserId(userId);
        if (existingKeyPair.isPresent()) {
            throw new UserAlreadyHasKeyPairException(userId);
        }

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        java.security.KeyPair keyPair = keyGen.genKeyPair();

        KeyPair keyPairEntity = new KeyPair();
        keyPairEntity.setUserId(userId);
        keyPairEntity.setPublicKey(keyPair.getPublic().getEncoded());

        try {
            String encryptedPrivateKey = EncryptionUtil.encrypt(
                    Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                    encryptionPassword
            );
            keyPairEntity.setPrivateKey(encryptedPrivateKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error encrypting private key", e);
            throw new RuntimeException("Error encrypting private key", e);
        }

        KeyPair savedKeyPair = keyPairRepository.save(keyPairEntity);
        logger.info("Generated key pair for user: {}", userId);
        return savedKeyPair;
    }

    public Optional<KeyPair> getKeyPair(String userId) {
        return keyPairRepository.findByUserId(userId).map(keyPairEntity -> {
            try {
                String decryptedPrivateKey = EncryptionUtil.decrypt(
                        new String(keyPairEntity.getPrivateKey(), StandardCharsets.UTF_8),
                        encryptionPassword
                );
                keyPairEntity.setPrivateKey(Base64.getDecoder().decode(decryptedPrivateKey));
                logger.info("Decrypted private key for user: {}", userId);
            } catch (Exception e) {
                logger.error("Error decrypting private key for user: {}", userId, e);
                throw new RuntimeException("Error decrypting private key", e);
            }
            return keyPairEntity;
        });
    }
}
