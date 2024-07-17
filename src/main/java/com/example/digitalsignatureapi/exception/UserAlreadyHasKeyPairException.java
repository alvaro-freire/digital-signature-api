package com.example.digitalsignatureapi.exception;

public class UserAlreadyHasKeyPairException extends RuntimeException {
    public UserAlreadyHasKeyPairException(String userId) {
        super("User " + userId + " already has a key pair");
    }
}
