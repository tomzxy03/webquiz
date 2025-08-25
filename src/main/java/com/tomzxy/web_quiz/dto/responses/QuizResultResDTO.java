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
public class QuizResultResDTO {

    private Long id;
    private Long quizInstanceId;
    private String quizTitle;
    private Long userId;
    private String userName;
    private Double score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer skippedQuestions;
    private Integer timeSpentSeconds;
    private LocalDateTime completedAt;
    private Boolean isPassed;
    private String performanceNotes;
    
    // Computed fields from business logic
    private String performanceGrade;
    private String performanceLevel;
    private Boolean isAboveAverage;
    private Boolean isBelowAverage;
    private Double efficiencyScore;
    private Boolean isEfficient;
    private Boolean isSlowPaced;
    private Boolean isFastPaced;
    private Double completionRate;
    private Boolean isFullyCompleted;
    private Boolean hasSkippedQuestions;
    private Double skipRate;
    private Boolean isHighSkipper;
    private Double averageTimePerQuestion;
    private Boolean isTimeEfficient;
    private Boolean isTimeConsuming;
    private String timeManagement;
    private Boolean isImprovementOverPrevious;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
} 