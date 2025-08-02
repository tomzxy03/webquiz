package com.tomzxy.web_quiz.dto.requests;

import com.tomzxy.web_quiz.enums.Gender;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileReqDTO {
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @EnumValidate(name = "gender", regex = "MALE|FEMALE|OTHER")
    private Gender gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String profilePictureUrl;
} 