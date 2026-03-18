package com.tomzxy.web_quiz.dto.requests.quiz;

import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.enums.QuizVisibility;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Quiz.QuestionLayout;
import com.tomzxy.web_quiz.models.Quiz.QuizConfig;
import com.tomzxy.web_quiz.validation.EnumValidate;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
public class QuizReqDTO {
    @NotBlank(message = "Quiz title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    private String description;

    @EnumValidate(name = "visibility", regex = "PUBLIC|PRIVATE|CLASS_ONLY")
    private QuizVisibility visibility;

    private Integer timeLimitMinutes;

    @Min(1)
    private Integer maxAttempt;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private QuizConfig quizConfig;

    private QuestionLayout questionLayout;


    @NotNull(message = "Subject ID is required")
    private Long subjectId;


} 