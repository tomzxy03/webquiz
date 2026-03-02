package com.tomzxy.web_quiz.controllers;

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
    @Operation(summary = "User logout", description = "Invalidate the current authentication token")
    public ResponseEntity<DataResDTO<Boolean>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractToken(authHeader);
        authService.logout(token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(true));
    }

    @PostMapping(ApiDefined.Auth.REFRESH)
    @Operation(summary = "Refresh token", description = "Exchange refresh token for new access token")
    public ResponseEntity<DataResDTO<AuthResDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenReqDTO refreshTokenReqDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(authService.refreshToken(refreshTokenReqDTO)));
    }

    @GetMapping(ApiDefined.Auth.ME)
    @Operation(summary = "Get current user", description = "Get the currently authenticated user's information")
    public ResponseEntity<DataResDTO<UserResDTO>> getMe(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractToken(authHeader);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(authService.getMe(token)));
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader != null ? authHeader : "";
    }
}