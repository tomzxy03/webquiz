package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.auth.LoginReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.RefreshTokenReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.SignupReqDTO;
import com.tomzxy.web_quiz.dto.responses.auth.AuthResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.ExistedException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.AuthMapper;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Improved token-based auth service with proper password encoding,
 * token expiration, and refresh token rotation.
 *
 * Note: In-memory storage (ConcurrentHashMap) is suitable for development/demo only.
 * For production, replace with JWT + Redis or database-backed solution.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // Token store: token -> TokenInfo
    private static final Map<String, TokenInfo> TOKEN_STORE = new ConcurrentHashMap<>();

    @Value("${auth.access-token-expiry-seconds:900}")      // 15 minutes default
    private long accessTokenExpirySeconds;

    @Value("${auth.refresh-token-expiry-seconds:2592000}") // 30 days default
    private long refreshTokenExpirySeconds;

    // Inner class to hold token metadata
    private record TokenInfo(Long userId, Instant expiry, TokenType type) {}
    private enum TokenType { ACCESS, REFRESH }

    @Override
    public AuthResDTO login(LoginReqDTO loginReqDTO) {
        User user = userRepo.findByEmail(loginReqDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + loginReqDTO.getEmail()));

        // Fixed: use BCrypt matches
        if (!passwordEncoder.matches(loginReqDTO.getPassword(), user.getPassword())) {
            throw new ApiException(AppCode.INVALID_CREDENTIALS, "Invalid credentials");
        }

        String accessToken = generateToken(user.getId(), TokenType.ACCESS, accessTokenExpirySeconds);
        String refreshToken = generateToken(user.getId(), TokenType.REFRESH, refreshTokenExpirySeconds);

        return AuthResDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toUserResDTO(user))
                .expiresIn(accessTokenExpirySeconds)
                .build();
    }

    @Override
    public AuthResDTO signup(SignupReqDTO signupReqDTO) {
        if (userRepo.existsByEmail(signupReqDTO.getEmail())) {
            throw new ExistedException(AppCode.EMAIL_ALREADY_REGISTERED, "Email already registered: " + signupReqDTO.getEmail());
        }
        if (userRepo.existsByUserName(signupReqDTO.getUserName())) {
            throw new ExistedException(AppCode.USERNAME_ALREADY_TAKEN, "Username already taken: " + signupReqDTO.getUserName());
        }

        User user = authMapper.toUser(signupReqDTO);
        user.setPassword(passwordEncoder.encode(signupReqDTO.getPassword())); // encode password

        user = userRepo.save(user);

        String accessToken = generateToken(user.getId(), TokenType.ACCESS, accessTokenExpirySeconds);
        String refreshToken = generateToken(user.getId(), TokenType.REFRESH, refreshTokenExpirySeconds);

        return AuthResDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toUserResDTO(user))
                .expiresIn(accessTokenExpirySeconds)
                .build();
    }

    @Override
    public void logout(String token) {
        TOKEN_STORE.remove(token);
        log.info("Token invalidated: {}", token);
    }

    @Override
    public AuthResDTO refreshToken(RefreshTokenReqDTO refreshTokenReqDTO) {
        String oldRefreshToken = refreshTokenReqDTO.getRefreshToken();
        TokenInfo info = TOKEN_STORE.get(oldRefreshToken);

        // Validate refresh token
        if (info == null || info.type() != TokenType.REFRESH || info.expiry().isBefore(Instant.now())) {
            throw new ApiException(AppCode.INVALID_CREDENTIALS, "Invalid or expired refresh token");
        }

        // Revoke old refresh token (rotation)
        TOKEN_STORE.remove(oldRefreshToken);

        // Fetch user
        User user = userRepo.findById(info.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Issue new tokens
        String newAccessToken = generateToken(user.getId(), TokenType.ACCESS, accessTokenExpirySeconds);
        String newRefreshToken = generateToken(user.getId(), TokenType.REFRESH, refreshTokenExpirySeconds);

        return AuthResDTO.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(userMapper.toUserResDTO(user))
                .expiresIn(accessTokenExpirySeconds)
                .build();
    }

    @Override
    public UserResDTO getMe(String token) {
        TokenInfo info = validateToken(token, TokenType.ACCESS);
        User user = userRepo.findById(info.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toUserResDTO(user);
    }

    /**
     * Validates an access token and returns its info.
     */
    private TokenInfo validateToken(String token, TokenType expectedType) {
        TokenInfo info = TOKEN_STORE.get(token);
        if (info == null) {
            throw new ApiException(AppCode.INVALID_CREDENTIALS, "Token not found");
        }
        if (info.type() != expectedType) {
            throw new ApiException(AppCode.INVALID_CREDENTIALS, "Invalid token type");
        }
        if (info.expiry().isBefore(Instant.now())) {
            // Automatically clean up expired token
            TOKEN_STORE.remove(token);
            throw new ApiException(AppCode.INVALID_CREDENTIALS, "Token expired");
        }
        return info;
    }

    /**
     * Generates a new token and stores it.
     */
    private String generateToken(Long userId, TokenType type, long expirySeconds) {
        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiry = Instant.now().plusSeconds(expirySeconds);
        TOKEN_STORE.put(token, new TokenInfo(userId, expiry, type));
        return token;
    }

    /**
     * Scheduled cleanup of expired tokens (runs every hour).
     */
    @Scheduled(fixedDelay = 3600000) // 1 hour
    public void cleanupExpiredTokens() {
        int count = 0;
        var now = Instant.now();
        var iterator = TOKEN_STORE.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().expiry().isBefore(now)) {
                iterator.remove();
                count++;
            }
        }
        if (count > 0) {
            log.info("Cleaned up {} expired tokens", count);
        }
    }
}