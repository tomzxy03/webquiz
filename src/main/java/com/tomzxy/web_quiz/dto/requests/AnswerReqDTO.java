package com.tomzxy.web_quiz.dto.requests;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerReqDTO {
    @NotBlank(message = "Answer text is required")
    @Size(min = 1, max = 500, message = "Answer text must be between 1 and 500 characters")
    private String answerText;

    @Size(max = 1000, message = "Answer description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Answer type is required")
    @EnumValidate(name = "answerType", regex = "TEXT|IMAGE")
    private QuestionAndAnswerType answerType;

    private boolean isCorrect = false;

    @Min(value = 0, message = "Points cannot be negative")
    @Max(value = 100, message = "Points cannot exceed 100")
    private Integer points = 0;

    @Min(value = 0, message = "Order index cannot be negative")
    private Integer orderIndex = 0;

    private String explanation;
}
