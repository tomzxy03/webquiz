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
    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 1000, message = "Question text must be between 10 and 1000 characters")
    private String questionText;

    @Size(max = 500, message = "Question description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Question type is required")
    @EnumValidate(name = "questionType", regex = "TEXT|IMAGE")
    private QuestionAndAnswerType questionType;

    @NotNull(message = "Question level is required")
    @EnumValidate(name = "level", regex = "EASY|MEDIUM|HARD")
    private Level level;

    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 100, message = "Points cannot exceed 100")
    private Integer points = 1;

    @Min(value = 10, message = "Time limit must be at least 10 seconds")
    @Max(value = 3600, message = "Time limit cannot exceed 1 hour")
    private Integer timeLimitSeconds;

    private String explanation;

    private String tags;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotEmpty(message = "Answers are required")
    private Set<AnswerReqDTO> answers;
}
