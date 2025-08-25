package com.tomzxy.web_quiz.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import com.tomzxy.web_quiz.models.Quiz.QuizInstance;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_instance_questions", indexes = {
    @Index(name = "idx_quiz_instance_question_instance", columnList = "quiz_instance_id"),
    @Index(name = "idx_quiz_instance_question_order", columnList = "display_order"),
    @Index(name = "idx_quiz_instance_question_original", columnList = "original_question_id")
})
public class QuizInstanceQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_instance_id", nullable = false)
    private QuizInstance quizInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_question_id", nullable = false)
    private Question originalQuestion;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder; // Thứ tự hiển thị trong instance này

    @Column(name = "points", nullable = false)
    private Integer points; // Điểm số cho câu hỏi này

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText; // Snapshot câu hỏi

    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType; // Loại câu hỏi

    @OneToMany(mappedBy = "quizInstanceQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizInstanceAnswer> answers = new HashSet<>();

    @OneToMany(mappedBy = "quizInstanceQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizUserResponse> userResponses = new HashSet<>();

    // Business Logic Methods
    public void addAnswer(QuizInstanceAnswer answer) {
        if (answer != null) {
            answers.add(answer);
            answer.setQuizInstanceQuestion(this);
        }
    }

    public void addUserResponse(QuizUserResponse response) {
        if (response != null) {
            userResponses.add(response);
            response.setQuizInstanceQuestion(this);
        }
    }

    public QuizInstanceAnswer getCorrectAnswer() {
        return answers.stream()
                .filter(QuizInstanceAnswer::isCorrect)
                .findFirst()
                .orElse(null);
    }

    public boolean isAnswered() {
        return userResponses.stream().anyMatch(QuizUserResponse::isAnswered);
    }

    public QuizUserResponse getUserResponse() {
        return userResponses.stream().findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "QuizInstanceQuestion{" +
                "id=" + getId() +
                ", displayOrder=" + displayOrder +
                ", points=" + points +
                ", questionText='" + (questionText != null ? questionText.substring(0, Math.min(30, questionText.length())) + "..." : "null") + '\'' +
                ", questionType='" + questionType + '\'' +
                '}';
    }
} 