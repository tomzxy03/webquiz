package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.requests.UserProfileReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;
import com.tomzxy.web_quiz.exception.GlobalExceptionHandler;
import com.tomzxy.web_quiz.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.User.BASE)

public class UserController {
    private final UserService userService;

    @GetMapping()
    public DataResDTO<PageResDTO<?>> getAllUser(@RequestParam @Min(0) int page ,@RequestParam @Min(10) int size){
        log.info("Get all users");
        return DataResDTO.ok(userService.get_users_pageable(page,size));
    }

    @GetMapping(ApiDefined.User.ID)
    public DataResDTO<UserResDTO> getUser(@PathVariable Long userId){
        log.info("get user by {}", userId);
        return DataResDTO.ok(userService.get_user(userId));
    }
    @PostMapping()
    public DataResDTO<UserResDTO> addUser(@Valid @RequestBody UserReqDto userReqDto){
        log.info("add user");
        return DataResDTO.create(userService.create_user(userReqDto));
    }

    @PutMapping(ApiDefined.User.ID)
    public DataResDTO<UserResDTO> updateUser(@PathVariable Long userId, @RequestBody @Valid UserReqDto userReqDto){
        log.info("Update user with id {}", userId);
        return DataResDTO.update(userService.update_user(userId,userReqDto));
    }

    @PutMapping(ApiDefined.Auth.UPDATE_INFO)
    public DataResDTO<UserResDTO> updateProfile(@RequestBody @Valid UserProfileReqDTO userProfileReqDTO){
        log.info("Update user profile");
        return DataResDTO.update(userService.update_profile(userProfileReqDTO));
    }

    @DeleteMapping(ApiDefined.User.ID)
    public DataResDTO<Void> deleteUser(@PathVariable Long userId){
        log.info("Delete user with id {}", userId);
        userService.delete_user(userId);
        return DataResDTO.delete();
    }
}
