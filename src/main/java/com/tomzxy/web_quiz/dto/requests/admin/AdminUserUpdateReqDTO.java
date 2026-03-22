package com.tomzxy.web_quiz.dto.requests.admin;

import com.tomzxy.web_quiz.enums.AdminUserStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminUserUpdateReqDTO {
    private AdminUserStatus status;
    private List<String> roles;
}
