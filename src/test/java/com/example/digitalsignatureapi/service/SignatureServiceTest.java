package com.example.digitalsignatureapi.service;

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
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class SignatureServiceTest {

    @Mock
    private KeyPairRepository keyPairRepository;

    @InjectMocks
    private SignatureService signatureService;

    @Value("${encryption.password}")
    private String encryptionPassword;

    private static final String USER_ID = "testuser";
    private static final String DOCUMENT = "ZXN0byBlcyB1biBkb2N1bWVudG8K";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(signatureService, "encryptionPassword", encryptionPassword);
    }

    @Test
    public void testSignDocument() throws Exception {
        // generate keypair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        java.security.KeyPair keyPair = keyGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(USER_ID);
        mockKeyPair.setPublicKey(publicKey.getEncoded());

        // encrypt private key
        String encryptedPrivateKey = EncryptionUtil.encrypt(Base64.getEncoder().encodeToString(privateKey.getEncoded()), encryptionPassword);
        mockKeyPair.setPrivateKey(encryptedPrivateKey.getBytes());

        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.of(mockKeyPair));

        String signature = signatureService.signDocument(USER_ID, DOCUMENT);
        assertNotNull(signature);
    }

    @Test
    public void testVerifySignature() throws Exception {
        // generate keypair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        java.security.KeyPair keyPair = keyGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(USER_ID);
        mockKeyPair.setPublicKey(publicKey.getEncoded());

        // encrypt private key
        String encryptedPrivateKey = EncryptionUtil.encrypt(Base64.getEncoder().encodeToString(privateKey.getEncoded()), encryptionPassword);
        mockKeyPair.setPrivateKey(encryptedPrivateKey.getBytes());

        when(keyPairRepository.findByUserId(anyString())).thenReturn(Optional.of(mockKeyPair));

        // sign the doc
        String signature = signatureService.signDocument(USER_ID, DOCUMENT);

        boolean isValid = signatureService.verifySignature(USER_ID, DOCUMENT, signature);
        assertTrue(isValid);
    }
}
