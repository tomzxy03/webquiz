package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.enums.QuizType;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.Index;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "quizzes", indexes = {
    @Index(name = "idx_quiz_host", columnList = "host_id"),
    @Index(name = "idx_quiz_subject", columnList = "subject_id"),
    @Index(name = "idx_quiz_type", columnList = "quiz_type"),
    @Index(name = "idx_quiz_created_at", columnList = "created_at")
})
public class Quiz extends BaseEntity {
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", nullable = false, length = 20)
    private QuizType quizType;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "passing_score_percentage")
    private Integer passingScorePercentage = 60;

    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "is_available", nullable = false)
    private boolean isQuizAvailable = true;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizQuestion> questions = new HashSet<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizResult> results = new HashSet<>();

    // Business Logic Methods
    public void addQuestion(QuizQuestion question) {
        if (question != null) {
            questions.add(question);
            question.setQuiz(this);
        }
    }

    public void removeQuestion(QuizQuestion question) {
        if (question != null && questions.remove(question)) {
            question.setQuiz(null);
        }
    }

    public void addQuestions(Collection<QuizQuestion> questionsList) {
        if (questionsList != null) {
            for (QuizQuestion question : questionsList) {
                addQuestion(question);
                
            }
        }
    }

    private void updateTotalQuestions() {
        this.totalQuestions = questions.size();
    }

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return isQuizAvailable && 
               (startDate == null || now.isAfter(startDate)) &&
               (endDate == null || now.isBefore(endDate));
    }

    public boolean isExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    public boolean isNotStarted() {
        return startDate != null && LocalDateTime.now().isBefore(startDate);
    }

    public long getRemainingTimeMinutes() {
        if (endDate == null) {
            return timeLimitMinutes != null ? timeLimitMinutes : Long.MAX_VALUE;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endDate)) {
            return 0;
        }
        return java.time.Duration.between(now, endDate).toMinutes();
    }

    public boolean canUserAttempt(User user) {
        if (!isAvailable()) {
            return false;
        }
        
        // Check if user has already reached max attempts
        long userAttempts = results.stream()
                .filter(result -> result.getUser().equals(user))
                .count();
        
        return userAttempts < maxAttempts;
    }

    public QuizResult getUserLatestResult(User user) {
        return results.stream()
                .filter(result -> result.getUser().equals(user))
                .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
                .orElse(null);
    }

    public double getAverageScore() {
        if (results.isEmpty()) {
            return 0.0;
        }
        return results.stream()
                .mapToInt(QuizResult::getScore)
                .average()
                .orElse(0.0);
    }

    public int getTotalParticipants() {
        return (int) results.stream()
                .map(QuizResult::getUser)
                .distinct()
                .count();
    }

    public void activate() {
        this.isQuizAvailable = true;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void deactivate() {
        this.isQuizAvailable = false;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isPassingScore(int score) {
        if (passingScorePercentage == null) {
            return score >= 60; // Default 60%
        }
        double percentage = (double) score / totalQuestions * 100;
        return percentage >= passingScorePercentage;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", quizType=" + quizType +
                ", totalQuestions=" + totalQuestions +
                ", isAvailable=" + isQuizAvailable +
                '}';
    }
}
