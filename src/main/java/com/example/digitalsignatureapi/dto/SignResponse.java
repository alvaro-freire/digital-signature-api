package com.example.digitalsignatureapi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignResponse {
    private String signature;

    public SignResponse(String signature) {
        this.signature = signature;
    }
}
