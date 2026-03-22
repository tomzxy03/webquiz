package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.user.UserReqDto;
import com.tomzxy.web_quiz.dto.requests.user.UserProfileReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.UserService;
import com.tomzxy.web_quiz.services.common.ConvertToPageResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public PageResDTO<?> get_users_pageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepo.findAllByActive(true, pageable);

        // Use MapStruct mapper instead of ModelMapper for better performance
        return convertToPageResDTO.convertPageResponse(users, pageable, userMapper::toUserResDTO);
    }

    @Override
    public UserResDTO create_user(UserReqDto userReqDto) {
        User user = userMapper.toUser(userReqDto);
        user.setPassword(passwordEncoder.encode(userReqDto.getPassword()));
        return userMapper.toUserResDTO(userRepo.save(user));
    }

    @Override
    public UserResDTO update_user(Long user_id, UserReqDto userReqDto) {
        User user = findUserById(user_id);
        userMapper.updateUser(user,userReqDto);

        return userMapper.toUserResDTO(userRepo.save(user));
    }

    @Override
    public UserResDTO get_user(Long user_id) {
        return userMapper.toUserResDTO(findUserById(user_id));
    }

    @Override
    public void delete_user(Long user_id) {
        User user = findUserById(user_id);
        user.setActive(false);
        try{
            userRepo.save(user);
        }catch (Exception e){
            throw new RuntimeException("Cannot delete user: ",e);
        }
    }

    @Override
    public void delete_user_list(List<Long> userId){
        List<User> users = userId.stream().map(
                userRepo::getReferenceById
        ).toList();
        for(User user: users){
            user.setActive(false);
        }
        userRepo.saveAll(users);
    }

    

    @Override
    public UserResDTO update_profile(Long user_id, UserProfileReqDTO userProfileReqDTO) {
       
        User user = findUserById(user_id);
        userMapper.updateUserProfile(user, userProfileReqDTO);
        
        return userMapper.toUserResDTO(user);
    }

    private User findUserById(Long id){
        return userRepo.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
    }



}
