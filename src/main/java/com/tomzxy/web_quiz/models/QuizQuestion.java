package com.tomzxy.web_quiz.models;

import lombok.*;

import java.util.List;

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

    String questionText;
    Boolean isCustom;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(name = "custom_question")
    String customQuestion;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = true)
    private Question question;

    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL)
    private List<QuizAnswer> quizAnswers;

    
}
