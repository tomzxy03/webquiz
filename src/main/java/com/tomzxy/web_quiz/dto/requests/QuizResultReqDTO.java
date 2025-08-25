package com.tomzxy.web_quiz.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultReqDTO {

    @NotNull(message = "Quiz instance ID is required")
    private Long quizInstanceId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Min(value = 0, message = "Score must be non-negative")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Double score;

    @Min(value = 0, message = "Total questions must be non-negative")
    private Integer totalQuestions;

    @Min(value = 0, message = "Correct answers must be non-negative")
    private Integer correctAnswers;

    @Min(value = 0, message = "Wrong answers must be non-negative")
    private Integer wrongAnswers;

    @Min(value = 0, message = "Skipped questions must be non-negative")
    private Integer skippedQuestions;

    @Min(value = 0, message = "Time spent must be non-negative")
    private Integer timeSpentSeconds;

    private LocalDateTime completedAt;

    private Boolean isPassed;

    private String performanceNotes;
} 