package com.tomzxy.web_quiz.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class QuizAnswerReqDTO {
    @NotNull(message = "Is custom is required")
    private boolean isCustom;

    @NotNull(message = "Is correct is required")
    private boolean isCorrect;

    private String customAnswer;

    private Long answerId; // nullable
} 