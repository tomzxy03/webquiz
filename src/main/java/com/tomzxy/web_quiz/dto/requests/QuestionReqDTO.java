package com.tomzxy.web_quiz.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tomzxy.web_quiz.enums.AnswerType;
import com.tomzxy.web_quiz.enums.ContentType;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class QuestionReqDTO {
    @NotBlank(message = "Question name is required")
    @Size(min = 1, max = 1000, message = "Question name must be between 10 and 1000 characters")
    private String questionName;

    @NotNull(message = "Question type is required")
    @EnumValidate(name = "questionType", regex = "TEXT|IMAGE|AUDIO|VIDEO")
    @JsonAlias("questionType")
    private ContentType type;

    @NotNull(message = "Answer type is required")
    @EnumValidate(name = "answerType", regex = "SINGLE_CHOICE|MULTIPLE_CHOICE|TRUE_FALSE|SHORT_ANSWER|FILL_IN_BLANK|MATCHING")
    private AnswerType answerType;

    @NotEmpty(message = "Answers are required")
    private Set<AnswerReqDTO> answers;
}
