package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    PageResDTO<?> get_users_pageable(int size , int page);

    UserResDTO create_user(UserReqDto userReqDto);

    UserResDTO update_user(Long user_id, UserReqDto userReqDto);

    UserResDTO get_user(Long user_id);

    void delete_user(Long user_id);
}
