package com.example.digitalsignatureapi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyResponse {
    private boolean isValid;

    public VerifyResponse(boolean isValid) {
        this.isValid = isValid;
    }

}