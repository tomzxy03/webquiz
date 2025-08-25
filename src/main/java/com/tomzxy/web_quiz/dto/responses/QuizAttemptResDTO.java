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
public class QuizAttemptResDTO {

    private Long id;
    private Long quizId;
    private String quizTitle;
    private Long userId;
    private String userName;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String status;
    private Integer currentQuestionIndex;
    private String answersSnapshot;
    private String timeSpentSnapshot;
    private String notes;
    
    // Computed fields from business logic
    private Boolean isInProgress;
    private Boolean isCompleted;
    private Boolean isAbandoned;
    private Long durationSeconds;
    private Double progressPercentage;
    private Boolean hasTimeLimit;
    private Boolean isTimeExpired;
    private Long remainingTimeSeconds;
    private Boolean canResume;
    private Boolean isFirstAttempt;
    private Integer attemptNumber;
    private Boolean isBestAttempt;
    private Boolean isLatestAttempt;
    private String attemptStatus;
    private Boolean needsResume;
    private Boolean canSubmit;
    private Boolean isSubmittable;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
} 