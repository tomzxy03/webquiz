package com.tomzxy.web_quiz.dto.requests;


import com.tomzxy.web_quiz.enums.Gender;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
public class PermissionReqDTO {

    @NotBlank
    String permissionName;


    String description;
}
