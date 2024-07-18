package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import com.example.digitalsignatureapi.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class SignatureService {

    private static final Logger logger = LoggerFactory.getLogger(SignatureService.class);

    @Autowired
    private KeyPairRepository keyPairRepository;

    @Value("${encryption.password}")
    private String encryptionPassword;

    public String signDocument(String userId, String document) throws Exception {
        KeyPair keyPairEntity = keyPairRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("User not found"));

        String decryptedPrivateKey = EncryptionUtil.decrypt(
                new String(keyPairEntity.getPrivateKey(), StandardCharsets.UTF_8), encryptionPassword);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(decryptedPrivateKey)));

        byte[] decodedDocument = Base64.getDecoder().decode(document);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(decodedDocument);

        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    public boolean verifySignature(String userId, String document, String signature) throws Exception {
        KeyPair keyPairEntity = keyPairRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("User not found"));

        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(keyPairEntity.getPublicKey()));

        byte[] decodedDocument = Base64.getDecoder().decode(document);

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(decodedDocument);

        byte[] decodedSignature = Base64.getDecoder().decode(signature);
        return sig.verify(decodedSignature);
    }
}
