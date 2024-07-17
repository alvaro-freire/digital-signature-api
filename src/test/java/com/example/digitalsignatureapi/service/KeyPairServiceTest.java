package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import com.example.digitalsignatureapi.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class KeyPairServiceTest {

    @Mock
    private KeyPairRepository keyPairRepository;

    @InjectMocks
    private KeyPairService keyPairService;

    private String userId = "testuser";
    private String encryptionPassword = "1234567890123456";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Establecer encryptionPassword en keyPairService
        ReflectionTestUtils.setField(keyPairService, "encryptionPassword", encryptionPassword);
    }

    @Test
    public void testGenerateKeyPair() throws NoSuchAlgorithmException {
        KeyPair keyPair = keyPairService.generateKeyPair(userId);
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublicKey());
        assertNotNull(keyPair.getPrivateKey());
    }

    @Test
    public void testGetKeyPair() {
        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(userId);
        mockKeyPair.setPublicKey("publicKey".getBytes());
        mockKeyPair.setPrivateKey("privateKey".getBytes());

        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.of(mockKeyPair));

        Optional<KeyPair> retrievedKeyPair = keyPairService.getKeyPair(userId);
        assertTrue(retrievedKeyPair.isPresent());
    }
}
