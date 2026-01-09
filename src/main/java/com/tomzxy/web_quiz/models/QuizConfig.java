package com.tomzxy.web_quiz.models;


import lombok.Data;

@Data
public class QuizConfig {
    private Boolean allowRetry;
    private Boolean shuffleQuestions;
    private Boolean shuffleAnswers;
    private Boolean showScoreImmediately;
    private Boolean allowReview;
    private Integer maxAttempts;
    private Integer passingScore;
}
