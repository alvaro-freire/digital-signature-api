package com.example.digitalsignatureapi.service;

import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.repository.KeyPairRepository;
import com.example.digitalsignatureapi.util.EncryptionUtil;
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
import java.util.Optional;

@Service
public class SignatureService {

    @Autowired
    private KeyPairRepository keyPairRepository;

    @Value("${encryption.password}")
    private String encryptionPassword;

    public String signDocument(String userId, String document) throws Exception {
        Optional<KeyPair> keyPairEntityOptional = keyPairRepository.findByUserId(userId);
        if (keyPairEntityOptional.isPresent()) {
            KeyPair keyPairEntity = keyPairEntityOptional.get();
            String decryptedPrivateKey = EncryptionUtil.decrypt(new String(keyPairEntity.getPrivateKey(), StandardCharsets.UTF_8), encryptionPassword);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(decryptedPrivateKey)));

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(document.getBytes());

            byte[] signedData = signature.sign();
            return Base64.getEncoder().encodeToString(signedData);
        } else {
            throw new Exception("User not found");
        }
    }

    public boolean verifySignature(String userId, String document, String signature) throws Exception {
        Optional<KeyPair> keyPairEntityOptional = keyPairRepository.findByUserId(userId);
        if (keyPairEntityOptional.isPresent()) {
            KeyPair keyPairEntity = keyPairEntityOptional.get();
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyPairEntity.getPublicKey()));

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(document.getBytes());

            byte[] decodedSignature = Base64.getDecoder().decode(signature);
            return sig.verify(decodedSignature);
        } else {
            throw new Exception("User not found");
        }
    }
}
