package com.tomzxy.web_quiz.models;

import lombok.*;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "quiz_answer")
public class QuizAnswer {


    private boolean isCustom;

    private boolean isCorrect;
    
    private String customAnswer;

    @ManyToOne
    @JoinColumn(name = "quiz_question_id")
    @JsonIgnore
    private QuizQuestion quizQuestion;

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = true)
    private Answer answer;
}
