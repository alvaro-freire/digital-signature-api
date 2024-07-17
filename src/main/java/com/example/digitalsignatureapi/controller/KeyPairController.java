package com.example.digitalsignatureapi.controller;

import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.service.KeyPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/keys")
public class KeyPairController {

    @Autowired
    private KeyPairService keyPairService;

    @PostMapping("/generate")
    public ResponseEntity<KeyPair> generateKeyPair(@RequestParam String userId) {
        try {
            KeyPair keyPair = keyPairService.generateKeyPair(userId);
            return ResponseEntity.ok(keyPair);
        } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<KeyPair> getKeyPair(@PathVariable String userId) {
        return keyPairService.getKeyPair(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
