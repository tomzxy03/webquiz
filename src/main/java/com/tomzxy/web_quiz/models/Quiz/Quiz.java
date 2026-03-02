package com.tomzxy.web_quiz.models.Quiz;

import com.tomzxy.web_quiz.enums.QuizVisibility;
import com.tomzxy.web_quiz.enums.QuizStatus;
import com.tomzxy.web_quiz.models.*;

import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.Index;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "quizzes", indexes = {
        @Index(name = "idx_quiz_host", columnList = "host_id"),
        @Index(name = "idx_quiz_subject", columnList = "subject_id"),
        @Index(name = "idx_quiz_visibility", columnList = "visibility"),
        @Index(name = "idx_quiz_created_at", columnList = "created_at")
})
public class Quiz extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuizStatus status = QuizStatus.DRAFT;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", columnDefinition = "jsonb")
    private QuizConfig config;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private QuizVisibility visibility = QuizVisibility.PRIVATE;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizInstance> instances;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizQuestionLink> quizQuestionLinks = new HashSet<>();

    @Column(name = "max_attempt")
    private int maxAttempt;

    @Transient
    public Integer getTotalQuestions() {
        return this.quizQuestionLinks != null ? this.quizQuestionLinks.size() : 0;
    }

    @Transient
    public Long getTotalAttempts() {
        return this.instances != null ? (long) this.instances.size() : 0L;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", visibility=" + visibility +
                ", totalQuestions=" + getTotalQuestions() +
                ", isAvailable=" + isActive() +
                '}';
    }
}
