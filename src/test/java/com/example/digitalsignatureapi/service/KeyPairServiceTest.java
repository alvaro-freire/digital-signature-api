package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.exception.UserAlreadyHasKeyPairException;
import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import com.example.digitalsignatureapi.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class KeyPairServiceTest {

    @Mock
    private KeyPairRepository keyPairRepository;

    @InjectMocks
    private KeyPairService keyPairService;

    @Value("${encryption.password}")
    private String encryptionPassword;

    private static final String USER_ID = "testuser";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(keyPairService, "encryptionPassword", encryptionPassword);
    }

    @Test
    public void testGenerateKeyPair() throws NoSuchAlgorithmException {
        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(USER_ID);
        mockKeyPair.setPublicKey("publicKey".getBytes());
        try {
            String encryptedPrivateKey = EncryptionUtil.encrypt("privateKey", encryptionPassword);
            mockKeyPair.setPrivateKey(encryptedPrivateKey.getBytes());

            when(keyPairRepository.save(any(KeyPair.class))).thenReturn(mockKeyPair);

            KeyPair keyPair = keyPairService.generateKeyPair(USER_ID);
            assertNotNull(keyPair);
            assertNotNull(keyPair.getPublicKey());
            assertNotNull(keyPair.getPrivateKey());
        } catch (Exception e) {
            fail("Encryption error: " + e.getMessage());
        }
    }

    @Test
    public void testGenerateKeyPairUserAlreadyExists() {
        KeyPair existingKeyPair = new KeyPair();
        existingKeyPair.setUserId(USER_ID);
        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.of(existingKeyPair));

        assertThrows(UserAlreadyHasKeyPairException.class, () -> {
            keyPairService.generateKeyPair(USER_ID);
        });
    }

    @Test
    public void testGetKeyPair() {
        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(USER_ID);
        mockKeyPair.setPublicKey("publicKey".getBytes());
        try {
            String encryptedPrivateKey = EncryptionUtil.encrypt("privateKey", encryptionPassword);
            mockKeyPair.setPrivateKey(encryptedPrivateKey.getBytes());

            when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.of(mockKeyPair));

            Optional<KeyPair> keyPair = keyPairService.getKeyPair(USER_ID);
            assertTrue(keyPair.isPresent());
            assertEquals(USER_ID, keyPair.get().getUserId());
            assertNotNull(keyPair.get().getPrivateKey());
            assertNotNull(keyPair.get().getPublicKey());
        } catch (Exception e) {
            fail("Encryption error: " + e.getMessage());
        }
    }

    @Test
    public void testGetKeyPairNotFound() {
        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        Optional<KeyPair> keyPair = keyPairService.getKeyPair(USER_ID);
        assertFalse(keyPair.isPresent());
    }
}
