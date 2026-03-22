package com.tomzxy.web_quiz.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tomzxy.web_quiz.enums.ContentType;
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
    @EnumValidate(name = "answerType", regex = "TEXT|IMAGE|AUDIO|VIDEO")
    @JsonAlias("answerType")
    private ContentType type;

    private boolean answerCorrect;
}
