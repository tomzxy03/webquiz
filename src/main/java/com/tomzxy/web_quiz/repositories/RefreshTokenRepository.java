package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.RefreshToken;
import com.tomzxy.web_quiz.models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByJti(String jti);
    List<RefreshToken> findByUserAndRevokedFalseAndExpiredAtAfter(User user, Instant now);
    long countByUserAndRevokedFalseAndExpiredAtAfter(User user, Instant now);
    void deleteByExpiredAtBefore(Instant expiryThreshold);
}
