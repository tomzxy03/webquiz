package com.tomzxy.web_quiz.dto.requests;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerReqDTO {
    @NotBlank(message = "Answer name is required")
    @Size(min = 1, max = 500, message = "Answer name must be between 1 and 500 characters")
    private String answerName;

    @NotNull(message = "Answer type is required")
    @EnumValidate(name = "answerType", regex = "TEXT|IMAGE")
    private QuestionAndAnswerType answerType;

    private boolean answerCorrect ;
}
