package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResultDetailResDTO {
    private Long id;
    private String quizTitle;
    private String userName;
    private String groupName;
    private AdminResultStatus status;
    private Double score;
    private Integer totalPoints;
    private Integer earnedPoints;
    private String startedAt;
    private String submittedAt;
    private Integer durationSeconds;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer skippedCount;
}
