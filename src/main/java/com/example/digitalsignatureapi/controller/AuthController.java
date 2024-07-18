package com.example.digitalsignatureapi.controller;

import com.example.digitalsignatureapi.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Value("${api.password}")
    private String apiPassword;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String userId, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.equals("Bearer " + apiPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API password");
        }

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User ID is required");
        }

        try {
            String token = jwtService.generateToken(userId);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating token");
        }
    }
}