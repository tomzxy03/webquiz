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
    @Column(name = "answer_name", unique = true)
    String answerName;

    @Column(name = "answer_type")
    @Enumerated(EnumType.STRING)
    QuestionAndAnswerType answerType;

    @Column(name = "is_correct")
    boolean is_correct = false;

    @ManyToOne(fetch = FetchType.LAZY)
    Question question;
}
