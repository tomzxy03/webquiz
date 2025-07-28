package com.tomzxy.web_quiz.dto.requests;

import com.tomzxy.web_quiz.enums.QuizType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import java.util.Set;


@Getter
public class QuizReqDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Total question is required")
    private int totalQuestion;

    @NotNull(message = "Quiz type is required")
    private QuizType quizType;

    @NotNull(message = "Host ID is required")
    private Long hostId;

    private Long groupId; // nullable

    @NotEmpty(message = "Questions are required")
    private Set<QuizQuestionReqDTO> questions;
} 