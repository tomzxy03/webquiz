package com.tomzxy.web_quiz.dto.requests;


import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
public class UserReqDto {
    @NotBlank(message = "User name not blank")
    String name;
    @NotEmpty(message = "Phone not empty")
    String phone;
    @Email(message = "Email not correct form")
    String email;
    @EnumValidate(name = "gender", regex = "MALE|FEMALE|OTHER")
    String gender;

    Date dob;

    Set<UUID> roles;
}
