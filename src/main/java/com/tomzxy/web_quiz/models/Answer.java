package com.tomzxy.web_quiz.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Builder
@Table(name = "answers", indexes = {
    @Index(name = "idx_answer_question", columnList = "question_id"),
    @Index(name = "idx_answer_correct", columnList = "answer_correct"),
    @Index(name = "idx_answer_option_order", columnList = "option_order"),
    @Index(name = "idx_answer_option_label", columnList = "option_label")
})
public class Answer extends BaseEntity {
    
    @Column(name = "answer_name", nullable = false, length = 500)
    private String answerName;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_type", nullable = false, length = 20)
    private QuestionAndAnswerType answerType;

    @Column(name = "answer_correct", nullable = false)

    private boolean answerCorrect;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "option_order")
    private Integer optionOrder;

    @Column(name = "option_label", length = 10)
    private String optionLabel;



    // Business Logic Methods
    public boolean isTextAnswer() {
        return answerType == QuestionAndAnswerType.TEXT;
    }

    public boolean isImageAnswer() {
        return answerType == QuestionAndAnswerType.IMAGE;
    }

    public void markAsCorrect() {
        this.answerCorrect = true;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void markAsIncorrect() {
        this.answerCorrect = false;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void activate() {
        this.setActive(true);
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void deactivate() {
        this.setActive(false);
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public boolean isAvailable() {
        return isActive();
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
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
