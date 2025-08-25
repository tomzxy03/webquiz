package com.tomzxy.web_quiz.dto.responses;

import lombok.Data;
import java.util.Set;

@Data
public class QuizQuestionResDTO {
    private Long id;
    private String questionName;
    private Boolean isCustom;
    private String customQuestion;
    private Set<QuizAnswerResDTO> quizAnswers;
} 