package com.tomzxy.web_quiz.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptReqDTO {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String status; // IN_PROGRESS, COMPLETED, ABANDONED

    private Integer currentQuestionIndex;

    private String answersSnapshot; // JSON string of answers

    private String timeSpentSnapshot; // JSON string of time spent per question

    private String notes;
} 