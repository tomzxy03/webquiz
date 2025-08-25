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
public class QuizInstanceResDTO {
    
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Long userId;
    private String userName;
    private LocalDateTime startedAt;
    private Integer timeLimitMinutes;
    private boolean shuffleEnabled;
    private Integer totalPoints;
    private String status;
    private List<QuizInstanceQuestionResDTO> questions;
    private long remainingTimeSeconds;
} 