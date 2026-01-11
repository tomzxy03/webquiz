package com.tomzxy.web_quiz.models.QuizUser;

import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.snapshot.QuizQuestionSnapshot;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Index;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.User.User;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder    
@Table(name = "quiz_instances", indexes = {
    @Index(name = "idx_quiz_instance_quiz", columnList = "quiz_id"),
    @Index(name = "idx_quiz_instance_user", columnList = "user_id"),
    @Index(name = "idx_quiz_instance_status", columnList = "status"),
    @Index(name = "idx_quiz_instance_started", columnList = "started_at")
})
// the quizInstance be created whenever user join quiz. It's include general result of user.

public class QuizInstance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;


    @Builder.Default
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_snapshot", columnDefinition = "jsonb")
    private QuizQuestionSnapshot snapshot;


    @Builder.Default
    @Column(name = "earned_points", nullable = false)
    private Integer earnedPoints = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuizInstanceStatus status = QuizInstanceStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "quizInstance", cascade = {CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval = false)
    private Set<QuizUserResponse> userResponses = new HashSet<>();

    // Business Logic Methods

    public void markAsTimedOut() {
        this.status = QuizInstanceStatus.TIMED_OUT;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isInProgress() {
        return this.status == QuizInstanceStatus.IN_PROGRESS;
    }

    public boolean isSubmitted() {
        this.setEndedAt(LocalDateTime.now());
        return this.status == QuizInstanceStatus.SUBMITTED ;
    }

    public boolean isTimedOut() {
        return this.status == QuizInstanceStatus.TIMED_OUT;
    }

    public Long getElapsedTimeMinutes() {
        return java.time.Duration.between(startedAt, endedAt).toMinutes();
    }

    public double getScorePercentage() {
        if (totalPoints == 0) return 0.0;
        return (double) earnedPoints / totalPoints * 100;
    }


    public void addUserResponse(QuizUserResponse response) {
        if (response != null) {
            userResponses.add(response);
            response.setQuizInstance(this);
        }
    }

    @Override
    public String toString() {
        return "QuizInstance{" +
                "id=" + getId() +
                ", quizId=" + (quiz != null ? quiz.getId() : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", status=" + status +
                ", startedAt=" + startedAt +
                ", totalPoints=" + totalPoints +
                ", earnedPoints=" + earnedPoints +
                '}';
    }
} 