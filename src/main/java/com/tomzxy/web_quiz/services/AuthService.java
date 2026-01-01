package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.auth.LoginReqDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;

public interface AuthService {

    String login(LoginReqDTO loginReqDTO);
}
