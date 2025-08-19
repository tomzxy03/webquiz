package com.tomzxy.web_quiz.dto.requests;

import com.tomzxy.web_quiz.enums.QuizType;
import com.tomzxy.web_quiz.validation.EnumValidate;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class QuizReqDTO {
    @NotBlank(message = "Quiz title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    private String description;

    @Min(value = 1, message = "Total questions must be at least 1")
    @Max(value = 100, message = "Total questions cannot exceed 100")
    private Integer totalQuestions = 0;

    @EnumValidate(name = "quizType", regex = "PUBLIC|GROUP")
    private QuizType quizType;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 480, message = "Time limit cannot exceed 8 hours")
    private Integer timeLimitMinutes;


    @Min(value = 1, message = "Max attempts must be at least 1")
    @Max(value = 10, message = "Max attempts cannot exceed 10")
    private Integer maxAttempts = 1;

    private boolean isPublic = false;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "Host ID is required")
    private Long hostId;

    private Long lobbyId; // nullable

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotEmpty(message = "Questions are required")
    private Set<QuizQuestionReqDTO> questions;
} 