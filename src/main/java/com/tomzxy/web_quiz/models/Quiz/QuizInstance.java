package com.tomzxy.web_quiz.models.Quiz;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Index;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Type;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.enums.QuizOptions;
import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@Table(name = "quiz_instances", indexes = {
    @Index(name = "idx_quiz_instance_quiz", columnList = "quiz_id"),
    @Index(name = "idx_quiz_instance_user", columnList = "user_id"),
    @Index(name = "idx_quiz_instance_status", columnList = "status"),
    @Index(name = "idx_quiz_instance_started", columnList = "started_at")
})
public class QuizInstance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_id", nullable = true)
    private Lobby lobby;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Type(JsonBinaryType.class)
    @Column(name = "options", columnDefinition = "jsonb")
    private Set<QuizOptions> options = new HashSet<>();


    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "earned_points", nullable = false)
    private Integer earnedPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuizInstanceStatus status = QuizInstanceStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "quizInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizUserResponse> userResponses = new HashSet<>();

    // Business Logic Methods
    public void submit() {
        this.status = QuizInstanceStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void markAsTimedOut() {
        this.status = QuizInstanceStatus.TIMED_OUT;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isInProgress() {
        return this.status == QuizInstanceStatus.IN_PROGRESS;
    }

    public boolean isSubmitted() {
        return this.status == QuizInstanceStatus.SUBMITTED;
    }

    public boolean isTimedOut() {
        return this.status == QuizInstanceStatus.TIMED_OUT;
    }

    public long getElapsedTimeMinutes() {
        LocalDateTime endTime = submittedAt != null ? submittedAt : LocalDateTime.now();
        return java.time.Duration.between(startedAt, endTime).toMinutes();
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