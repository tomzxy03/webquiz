package com.tomzxy.web_quiz.dto.requests.quiz;


import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import lombok.Getter;

@Getter
public class QuizQuestionReqDTO {
    private Long questionId;

    private QuestionReqDTO questionReqDTO;

    private Long points;
}
