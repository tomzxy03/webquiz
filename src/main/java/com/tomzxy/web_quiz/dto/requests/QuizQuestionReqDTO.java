package com.tomzxy.web_quiz.dto.requests;

import lombok.Getter;
import java.util.List;


@Getter
public class QuizQuestionReqDTO {
    private String questionText;
    private Boolean isCustom;
    private String customQuestion;
    private Long questionId; // nullable
    private List<QuizAnswerReqDTO> quizAnswers;
} 