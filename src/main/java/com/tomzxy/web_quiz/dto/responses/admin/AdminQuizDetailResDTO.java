package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminQuizStatus;
import com.tomzxy.web_quiz.enums.AdminQuizVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminQuizDetailResDTO {
    private Long id;
    private String title;
    private String description;
    private String subject;
    private String ownerName;
    private String groupName;
    private AdminQuizStatus status;
    private AdminQuizVisibility visibility;
    private Integer questionCount;
    private Integer attemptsCount;
    private Integer timeLimitMinutes;
    private Integer maxAttempt;
    private String startDate;
    private String endDate;
    private String createdAt;
}
