package com.tomzxy.web_quiz.dto.requests.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenReqDTO {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
