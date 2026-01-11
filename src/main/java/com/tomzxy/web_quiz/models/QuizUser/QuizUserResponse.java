package com.tomzxy.web_quiz.models.QuizUser;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.tomzxy.web_quiz.enums.ResponseStatus;
import com.tomzxy.web_quiz.models.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "quiz_user_responses",
        indexes = {
                @Index(name = "idx_quiz_user_response_instance", columnList = "quiz_instance_id"),
                @Index(name = "idx_quiz_user_response_instance_correct", columnList = "quiz_instance_id, is_correct"),
                @Index(name = "idx_quiz_user_response_instance_answered", columnList = "quiz_instance_id, answered_at")
        }
)
public class QuizUserResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_instance_id", nullable = false)
    private QuizInstance quizInstance;

    @Column(name = "selected_answer_id")
    private Long selectedAnswerId; // ID của đáp án user chọn

    @Column(name = "selected_answer_text", columnDefinition = "TEXT")
    private String selectedAnswerText; // Snapshot text của đáp án user chọn

    @Builder.Default
    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Builder.Default
    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned = 0;

    @Column(name = "response_time_seconds")
    private Integer responseTimeSeconds; // Thời gian trả lời (giây)

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Builder.Default
    @Column(name = "is_skipped", nullable = false)
    private boolean isSkipped = false;


    public ResponseStatus getStatus() {
        if (isSkipped) {
            return ResponseStatus.SKIPPED;
        } else if (isCorrect) {
            return ResponseStatus.CORRECT;
        } else {
            return ResponseStatus.INCORRECT;
        }
    }

    @Override
    public String toString() {
        return "QuizUserResponse{" +
                "id=" + getId() +
                ", quizInstanceId=" + (quizInstance != null ? quizInstance.getId() : "null") +
                ", isCorrect=" + isCorrect +
                ", isSkipped=" + isSkipped +
                ", pointsEarned=" + pointsEarned +
                ", status=" + getStatus() +
                '}';
    }
} 