package com.tomzxy.web_quiz.dto.responses.answer;

import com.tomzxy.web_quiz.enums.ContentType;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AnswerFileResDTO {

    private Long id;

    private String answerText;

    private ContentType answerType;

    private Boolean isCorrect;

    private Integer orderIndex;
}