package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminGroupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminGroupDetailResDTO {
    private Long id;
    private String name;
    private String ownerName;
    private String codeInvite;
    private Integer memberCount;
    private Integer quizCount;
    private Integer announcementCount;
    private AdminGroupStatus status;
    private String createdAt;
    private List<AdminMemberResDTO> members;
    private List<AdminQuizResDTO> quizzes;
}
