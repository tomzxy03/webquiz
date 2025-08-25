package com.tomzxy.web_quiz.models;

import lombok.*;

import java.util.Collection;
import java.util.HashSet;

import java.util.Set;

import com.tomzxy.web_quiz.models.Quiz.Quiz;

import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "quiz_question")
public class QuizQuestion extends BaseEntity {
    
    @Column(name = "is_custom", nullable = false)
    Boolean isCustom;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(name = "custom_question", nullable = true)
    String customQuestion;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = true)
    private Question question;

    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL)
    private Set<QuizAnswer> quizAnswers;

    public void saveAnswers(Collection<QuizAnswer> answersList) {
        if (answersList == null || answersList.isEmpty()) return;
        if (quizAnswers == null) {
            quizAnswers = new HashSet<>();
        }
        for (QuizAnswer answer : answersList) {
            quizAnswers.add(answer);
            answer.setQuizQuestion(this);
        }
    }
}
