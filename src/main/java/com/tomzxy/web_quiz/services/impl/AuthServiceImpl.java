package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.configs.security.CustomUserDetails;
import com.tomzxy.web_quiz.dto.requests.DeviceInfo;
import com.tomzxy.web_quiz.dto.requests.auth.LoginReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.RefreshTokenReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.SignupReqDTO;
import com.tomzxy.web_quiz.dto.responses.TokenResDTO;
import com.tomzxy.web_quiz.dto.responses.auth.AuthResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.exception.ExistedException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.exception.UnAuthorizedException;
import com.tomzxy.web_quiz.mapstructs.AuthMapper;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.QuestionBankRepo;
import com.tomzxy.web_quiz.repositories.RoleRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.AuthService;
import com.tomzxy.web_quiz.services.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepo roleRepo;
    private final QuestionBankRepo questionBankRepo;

    @Value("${jwt.access-expiration}")
    private long accessExpirationMs;              

    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Autowired
    private HttpServletRequest request;

    @Override
    public AuthResDTO login(LoginReqDTO loginReqDTO) {
        log.info("User login attempt: {}", loginReqDTO.getEmail());
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReqDTO.getEmail(),
                        loginReqDTO.getPassword()
                )
        );
        
        // Retrieve the authenticated user from the principal (assumes CustomUserDetails)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepo.findById(userDetails.id())
                .orElseThrow(() -> new NotFoundException("User not found"));

        log.info("User login successful: {} (ID: {})", loginReqDTO.getEmail(), user.getId());

        // Extract device information from request
        DeviceInfo deviceInfo = extractDeviceInfo();

        // Create refresh token (and access token) via RefreshTokenService
        TokenResDTO tokens = refreshTokenService.createRefreshToken(user, deviceInfo);

        return AuthResDTO.builder()
                .token(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .user(userMapper.toUserResDTO(user))
                .expiresIn(accessExpirationMs)
                .build();
    }

    @Override
    @Transactional
    public AuthResDTO signup(SignupReqDTO signupReqDTO) {
        log.info("User signup attempt: {}", signupReqDTO.getEmail());
        
        // Check if user already exists
        if (userRepo.existsByEmail(signupReqDTO.getEmail())) {
            log.warn("Signup failed: Email already registered - {}", signupReqDTO.getEmail());
            throw new ExistedException(AppCode.EMAIL_ALREADY_REGISTERED,
                    "User already exists with email: " + signupReqDTO.getEmail());
        }

        // Map and encode password
        User user = authMapper.toUser(signupReqDTO);
        user.setPassword(passwordEncoder.encode(signupReqDTO.getPassword()));
        
        // Assign USER role
        Role role = roleRepo.findByName("USER")
                .orElseThrow(() -> new NotFoundException("Role not found: USER"));
        user.setRoles(Set.of(role));

        // Save user
        user = userRepo.save(user);
        log.info("User created successfully: {} (ID: {})", signupReqDTO.getEmail(), user.getId());

        // Auto-create QuestionBank for new user
        createQuestionBankForUser(user);
        log.info("QuestionBank created automatically for user: {} (ID: {})", user.getEmail(), user.getId());

        // Extract device information
        DeviceInfo deviceInfo = extractDeviceInfo();

        // Create tokens
        TokenResDTO tokens = refreshTokenService.createRefreshToken(user, deviceInfo);

        return AuthResDTO.builder()
                .token(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .user(userMapper.toUserResDTO(user))
                .expiresIn(accessExpirationMs)
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        // Revoke the refresh token (and optionally blacklist access token)
        refreshTokenService.revokeRefreshToken(refreshToken);
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    @Override
    public AuthResDTO refreshToken(RefreshTokenReqDTO dto) {
        // Rotate refresh token and get new tokens
        TokenResDTO tokens = refreshTokenService.rotateRefreshToken(dto.getRefreshToken());

        // Extract username from the new access token (or refresh token)
        String username = jwtService.extractUsername(tokens.accessToken());

        // Fetch user for response
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        return AuthResDTO.builder()
                .token(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .user(userMapper.toUserResDTO(user))
                .expiresIn(accessExpirationMs)
                .build();
    }

    @Override
    public UserResDTO getMe(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userMapper.toUserResDTO(user);
    }

    /**
     * Auto-create QuestionBank for new user during signup
     * Each user has exactly one QuestionBank
     */
    private void createQuestionBankForUser(User user) {
        try {
            // Check if user already has a QuestionBank
            if (questionBankRepo.existsByOwnerId(user.getId())) {
                log.warn("QuestionBank already exists for user: {}", user.getId());
                return;
            }

            // Create new QuestionBank
            QuestionBank questionBank = QuestionBank.builder()
                    .owner(user)
                    .folders(new ArrayList<>())
                    .questions(new HashSet<>())
                    .build();

            questionBankRepo.save(questionBank);
            log.info("QuestionBank created for user: {} (email: {}) with bank ID: {}", 
                    user.getId(), user.getEmail(), questionBank.getId());
        } catch (Exception e) {
            log.error("Error creating QuestionBank for user: {}", user.getId(), e);
            throw new RuntimeException("Failed to create QuestionBank for user", e);
        }
    }

    /**
     * Extracts device information from the current HTTP request.
     * Uses RequestContextHolder to obtain the request when not explicitly passed.
     * Note: This approach ties the service to the web layer; consider passing DeviceInfo
     * as a parameter from the controller for better testability.
     */
    private DeviceInfo extractDeviceInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // Fallback for non-web contexts (e.g., tests)
            return new DeviceInfo("unknown", "unknown", "0.0.0.0", "unknown");
        }
        HttpServletRequest request = attributes.getRequest();
        String deviceId = request.getHeader("X-Device-ID");
        String deviceName = request.getHeader("X-Device-Name");
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        return new DeviceInfo(
                deviceId != null ? deviceId : "unknown",
                deviceName != null ? deviceName : "unknown",
                ipAddress,
                userAgent != null ? userAgent : "unknown"
        );
    }
}