package com.trvankiet.app.repository;

import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByCredential(Credential credential);
    Optional<Token> findByToken(String token);
    @Query("SELECT t FROM Token t WHERE t.credential.id = :credentialId " +
            "AND t.type = :tokenType AND t.isRevoked = false AND t.isExpired = false")
    List<Token> findActiveRefreshTokens(@Param("credentialId") String credentialId,
                                        @Param("tokenType") TokenType tokenType);

}
