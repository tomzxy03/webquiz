package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.configs.security.CustomUserDetails;
import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.auth.LoginReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.RefreshTokenReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.SignupReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.auth.AuthResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Auth.BASE)
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping(ApiDefined.Auth.LOGIN)
    @Operation(summary = "User login", description = "Authenticate user with email and password")
    public ResponseEntity<DataResDTO<AuthResDTO>> login(@Valid @RequestBody LoginReqDTO loginReqDTO) {
        log.info("User login attempt: {}", loginReqDTO.getEmail());
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(authService.login(loginReqDTO)));
    }

    @PostMapping(ApiDefined.Auth.SIGNUP)
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<DataResDTO<AuthResDTO>> signup(@Valid @RequestBody SignupReqDTO signupReqDTO) {
        log.info("User signup attempt: {}", signupReqDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(authService.signup(signupReqDTO)));
    }

    @PostMapping(ApiDefined.Auth.LOGOUT)
    @Operation(summary = "User logout", description = "Revoke refresh token")
    public ResponseEntity<DataResDTO<Boolean>> logout(
            @Valid @RequestBody RefreshTokenReqDTO refreshTokenReqDTO) {

        authService.logout(refreshTokenReqDTO.getRefreshToken());

        return ResponseEntity.ok(DataResDTO.ok(true));
    }

    @PostMapping(ApiDefined.Auth.REFRESH)
    @Operation(summary = "Refresh token", description = "Exchange refresh token for new access token")
    public ResponseEntity<DataResDTO<AuthResDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenReqDTO refreshTokenReqDTO) {
        log.info("User refresh token attempt: {}", refreshTokenReqDTO.getRefreshToken());
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DataResDTO.ok(authService.refreshToken(refreshTokenReqDTO)));
        }catch (Exception e){
            log.error("Refresh token error", e); // log full stacktrace
            throw e;
        }
    }

    @GetMapping(ApiDefined.Auth.ME)
    @Operation(summary = "Get current user", description = "Get the currently authenticated user's information")
    public UserResDTO getMe(@AuthenticationPrincipal CustomUserDetails user) {
        return authService.getMe(user.id());
    }
}