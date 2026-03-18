package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.configs.security.AuthorityBuilder;
import com.tomzxy.web_quiz.configs.security.CustomUserDetails;
import com.tomzxy.web_quiz.dto.requests.DeviceInfo;
import com.tomzxy.web_quiz.dto.responses.TokenResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.InvalidRefreshTokenException;
import com.tomzxy.web_quiz.exception.RefreshTokenExpiredException;
import com.tomzxy.web_quiz.models.RefreshToken;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.RefreshTokenRepository;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.RefreshTokenService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;
    private final UserRepo userRepository; // để load user từ subject
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenValidity;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public TokenResDTO createRefreshToken(User user, DeviceInfo deviceInfo) {
        // 1. Giới hạn số lượng token active
        Instant now = Instant.now();
        long activeCount = refreshTokenRepository.countByUserAndRevokedFalseAndExpiredAtAfter(user, now);
        // config
        int maxActiveTokens = 5;
        if (activeCount >= maxActiveTokens) {
            // Xóa token cũ nhất (theo issuedAt)
            Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "issuedAt"));
            Page<RefreshToken> oldest = refreshTokenRepository.findByUserAndRevokedFalseAndExpiredAtAfter(user, now, pageable);
            if (!oldest.isEmpty()) {
                RefreshToken toRevoke = oldest.getContent().get(0);
                toRevoke.setRevoked(true);
                refreshTokenRepository.save(toRevoke);
                log.info("Revoked oldest refresh token for user {}", user.getId());
            }
        }

        // 2. Tạo jti (UUID)
        String jti = UUID.randomUUID().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // 3. Tạo JWT refresh token
        String refreshTokenString = jwtService.generateRefreshToken(userDetails, jti);

        // 4. Lưu entity
        RefreshToken entity = new RefreshToken();
        entity.setJti(jti);
        entity.setUser(user);
        entity.setIssuedAt(now);
        entity.setExpiredAt(now.plus(Duration.ofSeconds(refreshTokenValidity)));
        entity.setRevoked(false);
        entity.setDeviceId(deviceInfo.getDeviceId());
        entity.setDeviceName(deviceInfo.getDeviceName());
        entity.setIpAddress(deviceInfo.getIpAddress());
        entity.setUserAgent(deviceInfo.getUserAgent());
        entity.setLastUsedAt(now);
        refreshTokenRepository.save(entity);

        // 5. Tạo access token (dùng method hiện có)
        String accessToken = jwtService.generateAccessToken(userDetails);

        return new TokenResDTO(accessToken, refreshTokenString);
    }

    @Override
@Transactional
public TokenResDTO rotateRefreshToken(String refreshToken) {

    // 1. Verify refresh token
    try {
        if (!jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
    } catch (JwtException e) {
        throw new ApiException(AppCode.BAD_REQUEST, e.getMessage());
    }

    // 2. Extract info từ JWT
    String jti = jwtService.extractJti(refreshToken);
    // 3. Tìm refresh token trong DB
    RefreshToken entity = refreshTokenRepository.findByJti(jti)
            .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

    // 4. Check revoked
    if (entity.isRevoked()) {
        revokeAllUserTokens(entity.getUser());
        throw new InvalidRefreshTokenException("Refresh token already revoked");
    }

    // 5. Check expired
    if (entity.getExpiredAt().isBefore(Instant.now())) {
        entity.setRevoked(true);
        refreshTokenRepository.save(entity);
        throw new RefreshTokenExpiredException("Refresh token expired");
    }

    // 6. Revoke token cũ (rotation)
    entity.setRevoked(true);
    entity.setLastUsedAt(Instant.now());
    refreshTokenRepository.save(entity);

    // 7. Lấy user
        User user = userRepository
                .findUserWithAuthorities(entity.getUser().getEmail())
                .orElseThrow();

        CustomUserDetails userDetails = new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), AuthorityBuilder.build(user));


    // 8. Tạo token mới
    String newJti = UUID.randomUUID().toString();

    String newAccessToken =
            jwtService.generateAccessToken(userDetails);

    String newRefreshToken =
            jwtService.generateRefreshToken(userDetails, newJti);

    // 9. Lưu refresh token mới
    RefreshToken newEntity = new RefreshToken();
    newEntity.setJti(newJti);
    newEntity.setUser(user);
    newEntity.setIssuedAt(Instant.now());
    newEntity.setExpiredAt(Instant.now().plusMillis(refreshTokenValidity));
    newEntity.setRevoked(false);

    // giữ device info
    newEntity.setDeviceId(entity.getDeviceId());
    newEntity.setDeviceName(entity.getDeviceName());
    newEntity.setIpAddress(entity.getIpAddress());
    newEntity.setUserAgent(entity.getUserAgent());
    newEntity.setLastUsedAt(Instant.now());

    refreshTokenRepository.save(newEntity);

    // 10. Return tokens
    return new TokenResDTO(newAccessToken, newRefreshToken);
}

    @Override
    public void revokeRefreshToken(String refreshToken) {
        String jti = jwtService.extractJti(refreshToken);
        RefreshToken entity = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));
        entity.setRevoked(true);
        refreshTokenRepository.save(entity);
    }

    @Transactional
    @Override
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllActiveTokens(user, Instant.now());
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteByExpiredAtBefore(Instant.now());
        log.info("Cleaned up expired refresh tokens before {}", Instant.now());
    }
}
