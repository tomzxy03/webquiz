package com.tomzxy.web_quiz.models;

import jakarta.persistence.*;

import lombok.*;
import jakarta.persistence.Index;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter 
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_results", indexes = {
    @Index(name = "idx_quiz_result_quiz", columnList = "quiz_id"),
    @Index(name = "idx_quiz_result_user", columnList = "user_id"),
    @Index(name = "idx_quiz_result_score", columnList = "score"),
    @Index(name = "idx_quiz_result_created_at", columnList = "created_at")
})
public class QuizResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @Column(name = "total_corrected", nullable = false)
    private Integer totalCorrected = 0;

    @Column(name = "total_failed", nullable = false)
    private Integer totalFailed = 0;

    @Column(name = "total_skipped", nullable = false)
    private Integer totalSkipped = 0;

    @Column(name = "completion_time_minutes")
    private Integer completionTimeMinutes;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "is_passed", nullable = false)
    private boolean isPassed = false;

    @Column(name = "percentage_score")
    private Double percentageScore = 0.0;

    @Column(name = "feedback", length = 1000)
    private String feedback;

    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttempt> attempts = new ArrayList<>();

    // Business Logic Methods
    public void startQuiz() {
        this.startedAt = LocalDateTime.now();
        this.isCompleted = false;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void completeQuiz() {
        this.completedAt = LocalDateTime.now();
        this.isCompleted = true;
        this.calculateCompletionTime();
        this.calculatePercentageScore();
        this.checkIfPassed();
        this.setUpdatedAt(LocalDateTime.now());
    }

    private void calculateCompletionTime() {
        if (startedAt != null && completedAt != null) {
            this.completionTimeMinutes = (int) java.time.Duration.between(startedAt, completedAt).toMinutes();
        }
    }

    private void calculatePercentageScore() {
        if (quiz != null && quiz.getTotalQuestions() > 0) {
            this.percentageScore = (double) score / quiz.getTotalQuestions() * 100;
        }
    }

    private void checkIfPassed() {
        if (quiz != null) {
            this.isPassed = quiz.isPassingScore(score);
        }
    }

    public void addAttempt(QuizAttempt attempt) {
        if (attempt != null) {
            attempts.add(attempt);
            attempt.setQuizResult(this);
            updateStatistics();
        }
    }

    private void updateStatistics() {
        this.totalCorrected = (int) attempts.stream()
                .filter(QuizAttempt::isCorrect)
                .count();
        
        this.totalFailed = (int) attempts.stream()
                .filter(attempt -> !attempt.isCorrect() && !attempt.isSkipped())
                .count();
        
        this.totalSkipped = (int) attempts.stream()
                .filter(QuizAttempt::isSkipped)
                .count();
        
        this.score = totalCorrected;
    }

    public boolean isInProgress() {
        return startedAt != null && !isCompleted;
    }

    public boolean isExpired() {
        if (quiz == null || quiz.getTimeLimitMinutes() == null) {
            return false;
        }
        
        if (startedAt == null) {
            return false;
        }
        
        LocalDateTime expiryTime = startedAt.plusMinutes(quiz.getTimeLimitMinutes());
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public long getRemainingTimeMinutes() {
        if (quiz == null || quiz.getTimeLimitMinutes() == null || startedAt == null) {
            return 0;
        }
        
        LocalDateTime expiryTime = startedAt.plusMinutes(quiz.getTimeLimitMinutes());
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(expiryTime)) {
            return 0;
        }
        
        return java.time.Duration.between(now, expiryTime).toMinutes();
    }

    public int getTotalQuestions() {
        return totalCorrected + totalFailed + totalSkipped;
    }

    public double getAccuracyPercentage() {
        int totalAnswered = totalCorrected + totalFailed;
        if (totalAnswered == 0) {
            return 0.0;
        }
        return (double) totalCorrected / totalAnswered * 100;
    }

    public boolean isPerfectScore() {
        return totalFailed == 0 && totalSkipped == 0 && totalCorrected > 0;
    }

    public boolean hasAttempts() {
        return !attempts.isEmpty();
    }

    public int getAttemptCount() {
        return attempts.size();
    }

    public QuizAttempt getLastAttempt() {
        if (attempts.isEmpty()) {
            return null;
        }
        return attempts.get(attempts.size() - 1);
    }

    public void addFeedback(String feedback) {
        this.feedback = feedback;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean hasFeedback() {
        return feedback != null && !feedback.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + getId() +
                ", quizId=" + (quiz != null ? quiz.getId() : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", score=" + score +
                ", percentageScore=" + percentageScore +
                ", isCompleted=" + isCompleted +
                ", isPassed=" + isPassed +
                '}';
    }
}
