package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.DeviceInfo;
import com.tomzxy.web_quiz.dto.responses.TokenResDTO;
import com.tomzxy.web_quiz.models.RefreshToken;
import com.tomzxy.web_quiz.models.User.User;

import java.util.Optional;

public interface RefreshTokenService {
    TokenResDTO createRefreshToken(User user, DeviceInfo deviceInfo);
    TokenResDTO rotateRefreshToken(String refreshToken);
    void revokeRefreshToken(String refreshToken);
    void revokeAllUserTokens(User user);
    void cleanupExpiredTokens();
}
