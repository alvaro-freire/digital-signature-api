package com.example.digitalsignatureapi.repository;

import com.example.digitalsignatureapi.model.KeyPairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyPairRepository extends JpaRepository<KeyPairEntity, Long> {
    Optional<KeyPairEntity> findByUserId(String userId);
}
