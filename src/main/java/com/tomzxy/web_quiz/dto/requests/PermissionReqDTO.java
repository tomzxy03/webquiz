package com.tomzxy.web_quiz.dto.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PermissionReqDTO {

    @NotBlank
    String permissionName;


    String description;
}
