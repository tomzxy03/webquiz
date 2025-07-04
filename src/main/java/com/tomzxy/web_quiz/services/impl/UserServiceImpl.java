package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final ConvertToPageResDTO convertToPageResDTO;

    @Override
    public PageResDTO<?> get_users_pageable(int size, int page) {
        Pageable pageable = PageRequest.of(size,page);
        Page<User> users = userRepo.findAllByActive(true, pageable);

//        List<UserResDTO> dtoList = users.stream()
//                .map(userMapper::toUserResDTO)
//                .toList();
//        return new PageResDTO<>(page,size,users.getTotalPages(),dtoList);
        return convertToPageResDTO.convertToPageResponse(users,page,size,UserResDTO.class);
    }

    @Override
    public UserResDTO create_user(UserReqDto userReqDto) {
        User user = userMapper.toUser(userReqDto);
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
        user.set_active(false);
        try{
            userRepo.save(user);
        }catch (Exception e){
            throw new RuntimeException("Cannot delete user: ",e);
        }
    }

    private User findUserById(Long id){
        return userRepo.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
    }

}
