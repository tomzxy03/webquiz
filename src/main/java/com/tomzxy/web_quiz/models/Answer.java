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
@Builder
@Table(name = "answers", indexes = {
    @Index(name = "idx_answer_question", columnList = "question_id"),
    @Index(name = "idx_answer_correct", columnList = "is_correct")
})
public class Answer extends BaseEntity {
    
    @Column(name = "answer_text", nullable = false, length = 500)
    private String answerText;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_type", nullable = false, length = 20)
    private QuestionAndAnswerType answerType;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Column(name = "points", nullable = false)
    private Integer points = 0;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    @Column(name = "explanation", length = 1000)
    private String explanation;

    @Column(name = "is_active", nullable = false)
    private boolean isAnswerActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<AnswerAttempt> answerAttempts = new java.util.HashSet<>();

    // Business Logic Methods
    public boolean isTextAnswer() {
        return answerType == QuestionAndAnswerType.TEXT;
    }

    public boolean isImageAnswer() {
        return answerType == QuestionAndAnswerType.IMAGE;
    }

    public void markAsCorrect() {
        this.isCorrect = true;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void markAsIncorrect() {
        this.isCorrect = false;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void activate() {
        this.isAnswerActive = true;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void deactivate() {
        this.isAnswerActive = false;
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public boolean isAvailable() {
        return isAnswerActive && isActive();
    }

    public boolean hasExplanation() {
        return explanation != null && !explanation.trim().isEmpty();
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    public boolean isPartialCredit() {
        return points > 0 && points < 100;
    }

    public boolean isFullCredit() {
        return points >= 100;
    }

    public boolean isNoCredit() {
        return points == 0;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + getId() +
                ", answerText='" + (answerText != null ? answerText.substring(0, Math.min(30, answerText.length())) + "..." : "null") + '\'' +
                ", isCorrect=" + isCorrect +
                ", points=" + points +
                ", isActive=" + isAnswerActive +
                '}';
    }
}
