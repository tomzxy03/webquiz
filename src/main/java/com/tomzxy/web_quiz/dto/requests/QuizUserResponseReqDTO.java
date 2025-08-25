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
public class QuizUserResponseReqDTO {

    @NotNull(message = "Quiz instance question ID is required")
    private Long quizInstanceQuestionId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long selectedAnswerId;

    private String userAnswer; // For text-based questions

    private Integer timeSpentSeconds;

    private Boolean isCorrect;

    private LocalDateTime answeredAt;

    private String notes;
} 