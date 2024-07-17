package com.example.digitalsignatureapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class KeyPairEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] publicKey;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] privateKey;

}
