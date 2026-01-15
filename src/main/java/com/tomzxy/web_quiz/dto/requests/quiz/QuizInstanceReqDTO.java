package com.tomzxy.web_quiz.dto.requests.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizInstanceReqDTO {
    
    private Long quizId;
    private Long userId;
    private Long lobbyId;
    private Set<QuizOptions> options;
    private Integer timeLimitMinutes;
    private Integer totalPoints;
    private LocalDateTime startedAt;

    
} 