package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminQuizStatus;
import com.tomzxy.web_quiz.enums.AdminQuizVisibility;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminQuizResDTO {
    private Long id;
    private String title;
    private String subject;
    private String ownerName;
    private String groupName;
    private AdminQuizStatus status;
    private AdminQuizVisibility visibility;
    private Integer questionCount;
    private Integer attemptsCount;
    private String createdAt;
}
