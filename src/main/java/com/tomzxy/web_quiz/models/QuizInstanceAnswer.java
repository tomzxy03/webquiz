package com.tomzxy.web_quiz.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_instance_answers", indexes = {
    @Index(name = "idx_quiz_instance_answer_question", columnList = "quiz_instance_question_id"),
    @Index(name = "idx_quiz_instance_answer_order", columnList = "display_order"),
    @Index(name = "idx_quiz_instance_answer_original", columnList = "original_answer_id")
})
public class QuizInstanceAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_instance_question_id", nullable = false)
    private QuizInstanceQuestion quizInstanceQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_answer_id", nullable = false)
    private Answer originalAnswer;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder; // Thứ tự hiển thị đáp án

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText; // Snapshot đáp án

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name = "option_label", length = 10)
    private String optionLabel; // A, B, C, D...

    // Business Logic Methods
    public boolean isSelectedByUser(QuizUserResponse userResponse) {
        if (userResponse == null) return false;
        return this.getId().equals(userResponse.getSelectedAnswerId());
    }

    @Override
    public String toString() {
        return "QuizInstanceAnswer{" +
                "id=" + getId() +
                ", displayOrder=" + displayOrder +
                ", answerText='" + (answerText != null ? answerText.substring(0, Math.min(30, answerText.length())) + "..." : "null") + '\'' +
                ", isCorrect=" + isCorrect +
                ", optionLabel='" + optionLabel + '\'' +
                '}';
    }
} 