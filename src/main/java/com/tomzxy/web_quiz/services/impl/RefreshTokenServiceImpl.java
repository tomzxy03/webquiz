package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.DeviceInfo;
import com.tomzxy.web_quiz.dto.responses.TokenResDTO;
import com.tomzxy.web_quiz.exception.InvalidRefreshTokenException;
import com.tomzxy.web_quiz.exception.RefreshTokenExpiredException;
import com.tomzxy.web_quiz.models.RefreshToken;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.RefreshTokenRepository;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.RefreshTokenService;
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
        // 1. Verify JWT và lấy thông tin
        if (!jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) { // cần method isTokenValid không cần UserDetails? hoặc tạo method riêng
            throw new InvalidRefreshTokenException("Invalid refresh token signature");
        }
        String jti = jwtService.extractJti(refreshToken);
        String username = jwtService.extractUsername(refreshToken);

        // 2. Tìm entity theo jti
        RefreshToken entity = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        // 3. Kiểm tra revoked và expired
        if (entity.isRevoked()) {
            revokeAllUserTokens(entity.getUser());
            throw new InvalidRefreshTokenException("Refresh token has been revoked");
        }
        if (entity.getExpiredAt().isBefore(Instant.now())) {
            entity.setRevoked(true);
            refreshTokenRepository.save(entity);
            throw new RefreshTokenExpiredException("Refresh token expired");
        }

        // 4. Cập nhật lastUsedAt
        entity.setLastUsedAt(Instant.now());

        // 5. Revoke token cũ (rotation)
        entity.setRevoked(true);
        refreshTokenRepository.save(entity);

        // 6. Tạo token mới (giữ nguyên device info)
        User user = entity.getUser();
        DeviceInfo deviceInfo = new DeviceInfo(
                entity.getDeviceId(),
                entity.getDeviceName(),
                entity.getIpAddress(),
                entity.getUserAgent()
        );

        // Gọi lại createRefreshToken nhưng không tạo access token mới? createRefreshToken đã tạo cả access và refresh.
        // Ta chỉ cần tạo refresh token mới và access token mới.
        String newJti = UUID.randomUUID().toString();
        String newRefreshTokenString = jwtService.generateRefreshToken((UserDetails) user, newJti);
        String newAccessToken = jwtService.generateAccessToken((UserDetails) user);

        RefreshToken newEntity = new RefreshToken();
        newEntity.setJti(newJti);
        newEntity.setUser(user);
        newEntity.setIssuedAt(Instant.now());
        newEntity.setExpiredAt(Instant.now().plus(Duration.ofSeconds(refreshTokenValidity)));
        newEntity.setRevoked(false);
        newEntity.setDeviceId(deviceInfo.getDeviceId());
        newEntity.setDeviceName(deviceInfo.getDeviceName());
        newEntity.setIpAddress(deviceInfo.getIpAddress());
        newEntity.setUserAgent(deviceInfo.getUserAgent());
        newEntity.setLastUsedAt(Instant.now());
        refreshTokenRepository.save(newEntity);

        return new TokenResDTO(newAccessToken, newRefreshTokenString);
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
