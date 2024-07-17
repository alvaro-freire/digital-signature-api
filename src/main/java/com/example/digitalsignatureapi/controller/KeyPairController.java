package com.example.digitalsignatureapi.controller;

import com.example.digitalsignatureapi.dto.DocumentRequest;
import com.example.digitalsignatureapi.dto.SignResponse;
import com.example.digitalsignatureapi.dto.VerifyRequest;
import com.example.digitalsignatureapi.exception.UserAlreadyHasKeyPairException;
import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.service.KeyPairService;
import com.example.digitalsignatureapi.service.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@RestController
@RequestMapping("/api/keys")
public class KeyPairController {

    private static final Logger logger = LoggerFactory.getLogger(KeyPairController.class);

    @Autowired
    private KeyPairService keyPairService;

    @Autowired
    private SignatureService signatureService;

    @PostMapping("/generate")
    public ResponseEntity<Object> generateKeyPair(@RequestParam String userId) {
        try {
            KeyPair keyPair = keyPairService.generateKeyPair(userId);
            return ResponseEntity.ok(keyPair);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating key pair", e);
            return ResponseEntity.status(500).body("Error generating key pair");
        } catch (UserAlreadyHasKeyPairException e) {
            logger.error("User already has a key pair: {}", userId);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<KeyPair> getKeyPair(@PathVariable String userId) {
        return keyPairService.getKeyPair(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sign")
    public ResponseEntity<SignResponse> signDocument(@RequestParam String userId, @RequestBody DocumentRequest documentRequest) {
        try {
            logger.info("Signing document for user: {}", userId);
            String signature = signatureService.signDocument(userId, documentRequest.getDocument());
            SignResponse response = new SignResponse(signature);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error signing document for user: {}", userId, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifySignature(@RequestParam String userId, @RequestBody VerifyRequest verifyRequest) {
        try {
            logger.info("Verifying signature for user: {}", userId);
            boolean isValid = signatureService.verifySignature(userId, verifyRequest.getDocument(), verifyRequest.getSignature());
            return ResponseEntity.ok(isValid);
        } catch (SignatureException e) {
            logger.error("Signature exception for user: {}", userId, e);
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            logger.error("Error verifying signature for user: {}", userId, e);
            return ResponseEntity.status(500).body(false);
        }
    }
}
