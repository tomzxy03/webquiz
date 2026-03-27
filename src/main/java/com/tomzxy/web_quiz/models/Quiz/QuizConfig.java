package com.tomzxy.web_quiz.models.Quiz;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizConfig {
    private Boolean shuffleQuestions;
    private Boolean shuffleAnswers;
    private Boolean autoDistributePoints;
    private Boolean showScoreImmediately;
    private Boolean allowReview;
    private Integer passingScore;
}
