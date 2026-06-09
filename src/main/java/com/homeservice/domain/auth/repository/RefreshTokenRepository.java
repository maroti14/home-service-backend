package com.homeservice.domain.auth.repository;


import org.springframework.data.jpa.repository
        .JpaRepository;
import org.springframework.data.jpa.repository
        .Modifying;
import org.springframework.data.jpa.repository
        .Query;
import org.springframework.transaction.annotation
        .Transactional;

import com.homeservice.domain.auth.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken>
        findByTokenAndIsRevokedFalse(String token);

    @Modifying @Transactional
    @Query("""
           UPDATE RefreshToken r
           SET r.isRevoked = true
           WHERE r.user.id = :userId
           """)
    void revokeAllByUserId(Long userId);

    @Modifying @Transactional
    @Query("""
           UPDATE RefreshToken r
           SET r.isRevoked = true
           WHERE r.token = :token
           """)
    void revokeByToken(String token);

    @Modifying @Transactional
    @Query("""
           DELETE FROM RefreshToken r
           WHERE r.expiresAt < :now
           OR r.isRevoked = true
           """)
    void deleteExpiredAndRevoked(LocalDateTime now);

    @Query("""
           SELECT COUNT(r) FROM RefreshToken r
           WHERE r.user.id = :userId
           AND r.isRevoked = false
           AND r.expiresAt > :now
           """)
    long countActiveByUserId(Long userId,
                              LocalDateTime now);
}