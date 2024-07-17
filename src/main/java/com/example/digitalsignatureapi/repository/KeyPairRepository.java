package com.example.digitalsignatureapi.repository;

import com.example.digitalsignatureapi.model.KeyPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyPairRepository extends JpaRepository<KeyPair, Long> {
    Optional<KeyPair> findByUserId(String userId);
}
