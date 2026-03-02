package com.tomzxy.web_quiz.controllers.user;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.user.UserReqDto;
import com.tomzxy.web_quiz.dto.requests.user.UserProfileReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.User.BASE)
@Tag(name = "Users", description = "User management APIs")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("")
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllUser(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (minimum 10)") @RequestParam(defaultValue = "10") @Min(10) int size){
        log.info("Get all users");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(userService.get_users_pageable(page,size)));
    }

    @GetMapping(ApiDefined.User.ID)
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DataResDTO<UserResDTO>> getUser(
            @Parameter(description = "User ID") @PathVariable Long userId){
        log.info("get user by {}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(userService.get_user(userId)));
    }
    
    @PostMapping("")
    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(schema = @Schema(implementation = DataResDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<UserResDTO>> addUser(@Valid @RequestBody UserReqDto userReqDto){
        log.info("add user");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create(userService.create_user(userReqDto)));
    }

    @PutMapping(ApiDefined.User.ID)
    @Operation(summary = "Update user", description = "Update an existing user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<UserResDTO>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long userId, 
            @RequestBody @Valid UserReqDto userReqDto){
        log.info("Update user with id {}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.update(userService.update_user(userId,userReqDto)));
    }

    @PutMapping(ApiDefined.Auth.UPDATE_INFO)
    @Operation(summary = "Update user profile", description = "Update the current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<UserResDTO>> updateProfile(@PathVariable Long userId, @RequestBody @Valid UserProfileReqDTO userProfileReqDTO){
        log.info("Update user profile");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.update(userService.update_profile(userId,userProfileReqDTO)));
    }

    @DeleteMapping(ApiDefined.User.ID)
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DataResDTO<Object>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long userId){
        log.info("Delete user with id {}", userId);
        userService.delete_user(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }
    @DeleteMapping(ApiDefined.User.DELETE_MANY)
    @Operation(summary = "Delete list user", description = "Delete the users by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DataResDTO<Object>> deleteUsers(@RequestBody List<Long> userId){
        log.info("Delete list user");
        userService.delete_user_list(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());

    }
}
