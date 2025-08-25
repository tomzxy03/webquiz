package com.tomzxy.web_quiz.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUserResponseResDTO {

    private Long id;
    private Long quizInstanceQuestionId;
    private String questionText;
    private Long userId;
    private String userName;
    private Long selectedAnswerId;
    private String selectedAnswerText;
    private String userAnswer;
    private Integer timeSpentSeconds;
    private Boolean isCorrect;
    private LocalDateTime answeredAt;
    private String notes;
    
    // Computed fields
    private Boolean isAnswered;
    private Boolean isSkipped;
    private Boolean isTimeEfficient;
    private Boolean isTimeConsuming;
    private String answerStatus;
    private Double timeEfficiency;
    private Boolean isFirstAttempt;
    private Boolean isLastAttempt;
    private Integer attemptCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
} 