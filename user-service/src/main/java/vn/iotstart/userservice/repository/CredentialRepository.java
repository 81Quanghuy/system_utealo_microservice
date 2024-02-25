package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, String> {
    Optional<Credential> findByUsername(String username);
}
