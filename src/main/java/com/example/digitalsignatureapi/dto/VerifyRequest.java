package com.example.digitalsignatureapi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyRequest {
    private String document;
    private String signature;
}
