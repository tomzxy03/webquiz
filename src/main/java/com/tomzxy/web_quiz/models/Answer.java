package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.Index;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "answers", indexes = {
    @Index(name = "idx_answer_question", columnList = "question_id"),
    @Index(name = "idx_answer_correct", columnList = "answer_correct"),
})
public class Answer extends BaseEntity {
    
    @Column(name = "answer_name", nullable = false, length = 500)
    private String answerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_type", nullable = false, length = 20)
    private QuestionAndAnswerType answerType;

    @Column(name = "answer_correct", nullable = false)
    private boolean answerCorrect;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Business Logic Methods
    public boolean isTextAnswer() {
        return answerType == QuestionAndAnswerType.TEXT;
    }

    public boolean isImageAnswer() {
        return answerType == QuestionAndAnswerType.IMAGE;
    }

    // to update correct answer
    public void markAsCorrect() {
        this.answerCorrect = true;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }
    // to update no correct answer
    public void markAsIncorrect() {
        this.answerCorrect = false;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public boolean isAvailable() {
        return isActive();
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + getId() +
                ", answerName='" + (answerName != null ? answerName.substring(0, Math.min(30, answerName.length())) + "..." : "null") + '\'' +
                ", answerCorrect=" + answerCorrect +
                ", isActive=" + isActive() +
                '}';
    }
}
