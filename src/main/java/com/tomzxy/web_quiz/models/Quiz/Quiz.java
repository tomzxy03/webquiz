package com.tomzxy.web_quiz.models.Quiz;

import com.tomzxy.web_quiz.enums.QuizAvailable;
import com.tomzxy.web_quiz.enums.QuizOptions;
import com.tomzxy.web_quiz.enums.QuizType;
import com.tomzxy.web_quiz.models.*;

import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.User.User;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.Index;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

//    @Column(name = "total_questions", nullable = false)
//    private Integer totalQuestions = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", nullable = false, length = 20)
    private QuizType quizType;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Version
    @Column(name = "version")
    private Long version = 0L;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_snapshot", columnDefinition = "jsonb")
    private Map<String, Object> questionSnapshot;


    @Column(name = "total_attempts") //total attempt of user
    private Integer totalAttempts = 0;

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

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "options", columnDefinition = "json")
    private Set<QuizOptions> options = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "available_type")
    private QuizAvailable quizAvailable; // trường hợp endTime == start time thì anytime và ngược lại.

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizInstance> instances;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizQuestionLink> quizQuestionLinks = new HashSet<>();



    @Transient
    public Integer getTotalQuestions() {
        return this.quizQuestionLinks != null ? this.quizQuestionLinks.size() : 0;
    }

//    public boolean isAvailable() {
//        LocalDateTime now = LocalDateTime.now();
//        if(!isActive()){
//            return false;
//        }
//        if(quizAvailable == QuizAvailable.SCHEDULED && (startDate == null || now.isAfter(startDate)) &&
//        (endDate == null || now.isBefore(endDate))){
//            return false;
//        }
//        return true;
//    }
//
//    public boolean isExpired() {
//        return endDate != null && LocalDateTime.now().isAfter(endDate);
//    }
//
//    public boolean isNotStarted() {
//        return startDate != null && LocalDateTime.now().isBefore(startDate);
//    }
//
//    public long getRemainingTimeMinutes() {
//        if (endDate == null) {
//            return timeLimitMinutes != null ? timeLimitMinutes : Long.MAX_VALUE;
//        }
//        LocalDateTime now = LocalDateTime.now();
//        if (now.isAfter(endDate)) {
//            return 0;
//        }
//        return java.time.Duration.between(now, endDate).toMinutes();
//    }

//    public boolean canUserAttempt(User user) {
//        if (!isAvailable()) {
//            return false;
//        }
//
//
//        // Check if quiz have been retried
//        return options.contains(QuizOptions.ALLOW_RETRY);
//    }
//
//    public QuizInstance getUserLatestInstance(User user) {
//        return instances.stream()
//                .filter(instance -> instance.getUser().equals(user))
//                .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
//                .orElse(null);
//    }
//
//
//    public void activate() {
//        this.setActive(true);
//        this.setUpdatedAt(LocalDateTime.now());
//    }
//
//    public void deactivate() {
//        this.setActive(false);
//        this.setUpdatedAt(LocalDateTime.now());
//    }
//
//
//    public boolean hasTimeLimit() {
//        return timeLimitMinutes != null && timeLimitMinutes > 0;
//    }


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
