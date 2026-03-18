package com.tomzxy.web_quiz.dto.requests.filter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizFilterReqDTO {
    private Long subjectId;
    @Size(max = 100)
    private String search;
    @Min(0)
    private Integer minQuestions;

    @Min(0)
    private Integer maxQuestions;

    @Min(0)
    private Integer minDuration;

    @Min(0)
    private Integer maxDuration;
}
