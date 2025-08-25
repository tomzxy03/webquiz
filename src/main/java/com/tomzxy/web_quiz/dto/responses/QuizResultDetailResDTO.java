package com.tomzxy.web_quiz.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDetailResDTO {
    
    private Long quizInstanceId;
    private Long quizId;
    private String quizTitle;
    private Long userId;
    private String userName;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer totalPoints;
    private Integer earnedPoints;
    private double scorePercentage;
    private String status;
    private long totalTimeSpentMinutes;
    private List<QuestionResultResDTO> questionResults;
} 