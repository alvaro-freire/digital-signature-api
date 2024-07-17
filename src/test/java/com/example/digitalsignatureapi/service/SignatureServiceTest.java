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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class SignatureServiceTest {

    @Mock
    private KeyPairRepository keyPairRepository;

    @InjectMocks
    private SignatureService signatureService;

    private String encryptionPassword = "1234567890123456"; // Ejemplo de 16 caracteres

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Establecer encryptionPassword en signatureService
        ReflectionTestUtils.setField(signatureService, "encryptionPassword", encryptionPassword);
    }

    @Test
    public void testSignDocument() throws Exception {
        String userId = "testuser";
        String document = "ZXN0byBlcyB1biBkb2N1bWVudG8K";

        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(userId);
        mockKeyPair.setPublicKey("publicKey".getBytes());
        mockKeyPair.setPrivateKey(EncryptionUtil.encrypt("privateKey", encryptionPassword).getBytes());

        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.of(mockKeyPair));

        String signature = signatureService.signDocument(userId, document);
        assertNotNull(signature);
    }

    @Test
    public void testVerifySignature() throws Exception {
        String userId = "testuser";
        String document = "ZXN0byBlcyB1biBkb2N1bWVudG8K";
        String signature = "signature";

        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(userId);
        mockKeyPair.setPublicKey("publicKey".getBytes());
        mockKeyPair.setPrivateKey(EncryptionUtil.encrypt("privateKey", encryptionPassword).getBytes());

        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.of(mockKeyPair));

        boolean isValid = signatureService.verifySignature(userId, document, signature);
        assertTrue(isValid);
    }
}
