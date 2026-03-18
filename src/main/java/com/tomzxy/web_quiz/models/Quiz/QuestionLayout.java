package com.tomzxy.web_quiz.models.Quiz;


import lombok.Data;

@Data
public class QuestionLayout {
    private String questionNumbering;
    private Integer questionPerPage;
    private Integer answerPerRow;
}
