package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.user.UserReqDto;
import com.tomzxy.web_quiz.dto.requests.user.UserProfileReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;

import java.util.List;

public interface UserService {
    PageResDTO<?> get_users_pageable(int size , int page);

    UserResDTO create_user(UserReqDto userReqDto);

    UserResDTO update_user(Long user_id, UserReqDto userReqDto);

    UserResDTO get_user(Long user_id);

    void delete_user(Long user_id);
    
    void delete_user_list(List<Long> userId);
    
    UserResDTO update_profile(Long user_id, UserProfileReqDTO userProfileReqDTO);
}
