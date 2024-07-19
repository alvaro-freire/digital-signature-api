package com.example.digitalsignatureapi.model;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int statusCode;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
    }

}