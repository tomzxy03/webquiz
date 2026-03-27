package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.RefreshToken;
import com.tomzxy.web_quiz.models.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByJti(String jti);
    Page<RefreshToken> findByUserAndRevokedFalseAndExpiredAtAfter(User user, Instant now, Pageable pageable);
    long countByUserAndRevokedFalseAndExpiredAtAfter(User user, Instant now);
    void deleteByExpiredAtBefore(Instant expiryThreshold);
    @Modifying
    @Query("""
    UPDATE RefreshToken r
    SET r.revoked = true
    WHERE r.user = :user
      AND r.revoked = false
      AND r.expiredAt > :now
""")
    int revokeAllActiveTokens(@Param("user") User user,
                              @Param("now") Instant now);
}
