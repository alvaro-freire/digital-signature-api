package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
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
        mockKeyPair.setPrivateKey("privateKey".getBytes());

        when(keyPairRepository.save(any(KeyPair.class))).thenReturn(mockKeyPair);

        KeyPair keyPair = keyPairService.generateKeyPair(USER_ID);
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublicKey());
        assertNotNull(keyPair.getPrivateKey());
    }
}
