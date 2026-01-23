package com.tomzxy.web_quiz.dto.requests.user;

import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileReqDTO {

    @NotNull(message = "user name must be not null")
    private String userName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private String profilePictureUrl;
} 