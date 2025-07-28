package com.tomzxy.web_quiz.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.tomzxy.web_quiz.enums.QuizType;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quiz extends BaseEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "total_question")
    private int totalQuestion;

    @Column(name = "quiz_type")
    @Enumerated(EnumType.STRING)
    private QuizType quizType;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private Lobby group;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private Set<QuizQuestion> questions;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private Set<QuizResult> submissions;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public void saveQuestions(Collection<QuizQuestion> questionsList) {
        if (questionsList == null || questionsList.isEmpty()) return;
        if (questions == null) {
            questions = new HashSet<>();
        }
        for (QuizQuestion question : questionsList) {
            questions.add(question);
            question.setQuiz(this);
        }
    }
    
}
