package com.tomzxy.web_quiz.dto.requests.auth;

import com.tomzxy.web_quiz.utils.RegexContains;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter

public class LoginReqDTO {
    @NotBlank
    @Email(message = "email not correct", regexp = RegexContains.Email)
    private String email;

    @NotBlank
    private String password;
}
