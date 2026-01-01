package com.tomzxy.web_quiz.services.impl;

import org.springframework.stereotype.Service;

import com.tomzxy.web_quiz.dto.requests.auth.LoginReqDTO;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepo userRepo;

    private final UserMapper userMapper;
 
    @Override
    public String login(LoginReqDTO loginReqDTO) {
        return null;
    }
}
