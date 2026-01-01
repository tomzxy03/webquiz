package com.tomzxy.web_quiz.models.Quiz;


import com.tomzxy.web_quiz.models.Question;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "quiz_question_links")
public class QuizQuestionLink {
    @EmbeddedId
    private QuizQuestionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("quizId")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionId")
    private Question question;

    @Column(name = "points", nullable = false)
    private Integer points = 1;

}
