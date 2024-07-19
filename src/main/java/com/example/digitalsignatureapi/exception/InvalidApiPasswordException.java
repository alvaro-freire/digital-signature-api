package com.example.digitalsignatureapi.exception;

public class InvalidApiPasswordException extends RuntimeException {
    public InvalidApiPasswordException() {
        super("Invalid API password");
    }
}
