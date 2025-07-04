package com.tomzxy.web_quiz.dto.requests;


import com.tomzxy.web_quiz.enums.Gender;
import com.tomzxy.web_quiz.utils.RegexContains;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
public class UserReqDto {
    @NotBlank(message = "User name not blank")
    String user_name;

    //@Pattern(regexp = RegexContains.Phone)
    String phone;

    @Email(message = "Email not correct form")
    String email;
    @EnumValidate(name = "gender", regex = "MALE|FEMALE|OTHER")
    Gender gender;

    Date dob;

    Set<UUID> roles;
}
