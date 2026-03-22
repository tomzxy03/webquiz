package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminUserStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResDTO {
    private Long id;
    private String userName;
    private String email;
    private List<String> roles;
    private AdminUserStatus status;
    private String createdAt;
    private String lastLoginAt;
    private Integer quizTakenCount;
    private Integer groupCount;
}
