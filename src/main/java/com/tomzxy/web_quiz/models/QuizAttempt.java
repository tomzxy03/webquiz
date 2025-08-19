package com.tomzxy.web_quiz.models;

import jakarta.persistence.*;

import lombok.*;
import jakarta.persistence.Index;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomzxy.web_quiz.dto.responses.AnswerSnapshot;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_attempts", indexes = {
    @Index(name = "idx_quiz_attempt_result", columnList = "quiz_result_id"),
    @Index(name = "idx_quiz_attempt_correct", columnList = "is_correct"),
    @Index(name = "idx_quiz_attempt_created_at", columnList = "created_at"),
    @Index(name = "idx_quiz_attempt_question_type", columnList = "question_type"),
    @Index(name = "idx_quiz_attempt_question_level", columnList = "question_level"),
    @Index(name = "idx_quiz_attempt_original_question", columnList = "original_question_id")
})
public class QuizAttempt extends BaseEntity {

    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @Column(name = "selected_answer", length = 1000)
    private String selectedAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Column(name = "is_skipped", nullable = false)
    private boolean isSkipped = false;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned = 0;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "feedback", length = 500)
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_result_id", nullable = false)
    private QuizResult quizResult;

    // === QUESTION SNAPSHOT ===
    @Column(name = "question_type", length = 20)
    private String questionType;

    @Column(name = "question_points", nullable = false)
    private Integer questionPoints = 1;

    @Column(name = "question_level", length = 20)
    private String questionLevel;

    // === ANSWER SNAPSHOT ===
    @Column(name = "correct_answer_text", length = 1000)
    private String correctAnswerText;

    @Column(name = "all_answer_options", columnDefinition = "TEXT")
    private String allAnswerOptions;

    // === RELATIONSHIPS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_question_id")
    private Question originalQuestion;

    // Business Logic Methods
    public void markAsCorrect() {
        this.isCorrect = true;
        this.isSkipped = false;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void markAsIncorrect() {
        this.isCorrect = false;
        this.isSkipped = false;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void markAsSkipped() {
        this.isCorrect = false;
        this.isSkipped = true;
        this.selectedAnswer = null;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void setAnswer(String answer) {
        this.selectedAnswer = answer;
        this.isSkipped = false;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isAnswered() {
        return selectedAnswer != null && !selectedAnswer.trim().isEmpty();
    }

    public boolean isNotAnswered() {
        return !isAnswered() && !isSkipped;
    }

    public boolean hasFeedback() {
        return feedback != null && !feedback.trim().isEmpty();
    }

    public void addFeedback(String feedback) {
        this.feedback = feedback;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isAnsweredCorrectly() {
        return isCorrect && !isSkipped;
    }

    public boolean isAnsweredIncorrectly() {
        return !isCorrect && !isSkipped && isAnswered();
    }

    public void calculateTimeTaken(LocalDateTime startTime) {
        if (startTime != null && answeredAt != null) {
            this.timeTakenSeconds = (int) java.time.Duration.between(startTime, answeredAt).getSeconds();
        }
    }

    public String getStatus() {
        if (isSkipped) {
            return "SKIPPED";
        } else if (isCorrect) {
            return "CORRECT";
        } else if (isAnswered()) {
            return "INCORRECT";
        } else {
            return "NOT_ANSWERED";
        }
    }

    // === SNAPSHOT METHODS ===
    public void snapshotQuestion(Question question) {
        if (question != null) {
            this.questionText = question.getQuestionName();
            this.questionType = question.getQuestionType().name();
            this.questionPoints = question.getPoints();
            this.questionLevel = question.getLevel().name();
        }
    }

    public void snapshotAnswers(Set<Answer> answers) {
        if (answers != null && !answers.isEmpty()) {
            try {
                List<AnswerSnapshot> answerSnapshots = answers.stream()
                    .map(answer -> AnswerSnapshot.builder()
                        .id(answer.getId())
                        .answerName(answer.getAnswerName())
                        .answerCorrect(answer.isAnswerCorrect())
                        .optionOrder(answer.getOptionOrder())
                        .optionLabel(answer.getOptionLabel())
                        .build())
                    .collect(Collectors.toList());
                
                // Use Jackson ObjectMapper for JSON conversion
                ObjectMapper objectMapper = new ObjectMapper();
                this.allAnswerOptions = objectMapper.writeValueAsString(answerSnapshots);
                
                // Store correct answer text
                this.correctAnswerText = answers.stream()
                    .filter(Answer::isAnswerCorrect)
                    .map(Answer::getAnswerName)
                    .collect(Collectors.joining("; "));
            } catch (Exception e) {
                // Fallback to simple text if JSON conversion fails
                this.correctAnswerText = answers.stream()
                    .filter(Answer::isAnswerCorrect)
                    .map(Answer::getAnswerName)
                    .collect(Collectors.joining("; "));
            }
        }
    }

    public void setUserResponse(String response, boolean isCorrect) {
        this.selectedAnswer = response;
        this.isCorrect = isCorrect;
        this.pointsEarned = isCorrect ? questionPoints : 0;
        this.answeredAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "QuizAttempt{" +
                "id=" + getId() +
                ", questionText='" + (questionText != null ? questionText.substring(0, Math.min(30, questionText.length())) + "..." : "null") + '\'' +
                ", isCorrect=" + isCorrect +
                ", isSkipped=" + isSkipped +
                ", pointsEarned=" + pointsEarned +
                ", status=" + getStatus() +
                '}';
    }
}
