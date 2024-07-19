package com.example.digitalsignatureapi.exception;

public class MissingHeaderException extends RuntimeException {
    public MissingHeaderException(String headerName) {
        super("Missing required header: " + headerName);
    }
}
