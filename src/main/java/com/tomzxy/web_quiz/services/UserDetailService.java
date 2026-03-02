package com.tomzxy.web_quiz.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailService {

    UserDetails loadUserByUsername(String username);
}
