package com.tomzxy.web_quiz.dto.requests;

import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class QuestionReqDTO {
    @NotBlank(message = "Question name is required")
    @Size(min = 10, max = 1000, message = "Question name must be between 10 and 1000 characters")
    private String questionName;

    private String description;

    @NotNull(message = "Question type is required")
    @EnumValidate(name = "questionType", regex = "TEXT|IMAGE")
    private QuestionAndAnswerType questionType;

    @NotNull(message = "Question level is required")
    @EnumValidate(name = "level", regex = "EASY|MEDIUM|HARD")
    private Level level;

    @NotNull(message = "Points are required")
    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 100, message = "Points cannot exceed 100")
    private Integer points;

    private Long subjectId;

    @NotEmpty(message = "Answers are required")
    private Set<AnswerReqDTO> answers;
}
