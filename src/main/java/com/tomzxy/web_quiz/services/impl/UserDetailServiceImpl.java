package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.configs.security.CustomUserDetails;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
