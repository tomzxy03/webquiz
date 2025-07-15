package com.tomzxy.web_quiz.models;


import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "Answer")
public class Answer extends BaseEntity{
    @Column(name = "answer_name")
    String answerName;

    @Column(name = "answer_type")
    @Enumerated(EnumType.STRING)
    QuestionAndAnswerType answerType;

    @Column(name = "is_correct")
    boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    Question question;
}
