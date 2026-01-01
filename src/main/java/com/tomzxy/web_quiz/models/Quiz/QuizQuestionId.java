package com.tomzxy.web_quiz.models.Quiz;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuizQuestionId implements Serializable {
    @Column(name = "quiz_id")
    private Long quizId;
    @Column(name = "question_id")
    private Long questionId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizQuestionId that = (QuizQuestionId) o;
        return Objects.equals(quizId, that.quizId) && Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, questionId);
    }
}
