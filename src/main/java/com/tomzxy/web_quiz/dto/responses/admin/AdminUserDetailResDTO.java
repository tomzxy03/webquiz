package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailResDTO {
    private Long id;
    private String userName;
    private String email;
    private String profilePictureUrl;
    private List<String> roles;
    private AdminUserStatus status;
    private String createdAt;
    private String lastLoginAt;
    private Integer quizTakenCount;
    private Integer groupCount;
    private List<AdminGroupResDTO> groups;
    private List<AdminResultResDTO> recentResults;
}
