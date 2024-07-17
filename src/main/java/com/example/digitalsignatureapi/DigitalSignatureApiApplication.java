package com.example.digitalsignatureapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "com.example.digitalsignatureapi")
@EntityScan
public class DigitalSignatureApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DigitalSignatureApiApplication.class, args);
    }
}
