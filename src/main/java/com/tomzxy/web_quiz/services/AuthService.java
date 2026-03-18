package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.auth.LoginReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.SignupReqDTO;
import com.tomzxy.web_quiz.dto.requests.auth.RefreshTokenReqDTO;
import com.tomzxy.web_quiz.dto.responses.auth.AuthResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;

public interface AuthService {
    AuthResDTO login(LoginReqDTO loginReqDTO);

    AuthResDTO signup(SignupReqDTO signupReqDTO);

    void logout(String token);

    AuthResDTO refreshToken(RefreshTokenReqDTO refreshTokenReqDTO);

    UserResDTO getMe(Long userID);
}
