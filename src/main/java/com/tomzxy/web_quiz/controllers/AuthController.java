package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.auth.LoginReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;
import com.tomzxy.web_quiz.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Auth.BASE)
public class AuthController {
    
    private final UserService userService;

    @PostMapping(ApiDefined.Auth.LOGIN)
    public DataResDTO<UserResDTO> login(@Valid @RequestBody LoginReqDTO loginReqDTO) {
        log.info("User login attempt: {}", loginReqDTO.getUserName());
        return DataResDTO.ok(userService.login(loginReqDTO));
    }
} 