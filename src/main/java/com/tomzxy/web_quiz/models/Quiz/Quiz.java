package com.tomzxy.web_quiz.models.Quiz;

import com.tomzxy.web_quiz.enums.QuizType;
import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.QuizQuestion;
import com.tomzxy.web_quiz.models.Subject;
import com.tomzxy.web_quiz.models.User;

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

    @Column(name = "max_attempts") // max attempts for a user to attempt the quiz
    private Integer maxAttempts = 1;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;


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
    private Set<QuizInstance> instances = new HashSet<>();

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
        return isActive() && 
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
        long userAttempts = instances.stream()
                .filter(instance -> instance.getUser().equals(user))
                .count();
        
        return userAttempts < maxAttempts;
    }

    public QuizInstance getUserLatestInstance(User user) {
        return instances.stream()
                .filter(instance -> instance.getUser().equals(user))
                .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
                .orElse(null);
    }


    public void activate() {
        this.setActive(true);
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void deactivate() {
        this.setActive(false);
        this.setUpdatedAt(LocalDateTime.now());
    }


    public boolean hasTimeLimit() {
        return timeLimitMinutes != null && timeLimitMinutes > 0;
    }

    public boolean isUnlimitedAttempts() {
        return maxAttempts == null || maxAttempts <= 0;
    }


    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", quizType=" + quizType +
                ", totalQuestions=" + totalQuestions +
                ", isAvailable=" + isActive() +
                '}';
    }
}
