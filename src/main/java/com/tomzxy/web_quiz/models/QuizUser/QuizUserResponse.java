package com.tomzxy.web_quiz.models.QuizUser;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
@Table(name = "quiz_user_responses", uniqueConstraints = {
        @UniqueConstraint(name = "uk_instance_question", columnNames = { "quiz_instance_id", "question_snapshot_key" })
}, indexes = {
        @Index(name = "idx_quiz_user_response_instance", columnList = "quiz_instance_id"),
        @Index(name = "idx_quiz_user_response_instance_correct", columnList = "quiz_instance_id, is_correct"),
        @Index(name = "idx_quiz_user_response_instance_answered", columnList = "quiz_instance_id, answered_at"),
        @Index(name = "idx_quiz_user_response_snapshot_key", columnList = "quiz_instance_id, question_snapshot_key")
})
public class QuizUserResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_instance_id", nullable = false)
    private QuizInstance quizInstance;

    @Column(name = "question_id", nullable = true)
    private Long questionId;

    @Column(name = "question_snapshot_key")
    private String questionSnapshotKey;

    @Column(name = "selected_answer_id")
    private Long selectedAnswerId; // ID của đáp án user chọn (single choice backward compat)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "selected_answer_ids", columnDefinition = "jsonb")
    @Builder.Default
    private List<Long> selectedAnswerIds = new ArrayList<>(); // Selected answer indices (snapshot-based)

    @Builder.Default
    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Builder.Default
    @Column(name = "points_earned", nullable = false)
    private Long pointsEarned = 0L;

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

    /** True if user selected an answer (by id or text). */
    public boolean isAnswered() {
        return !isSkipped && (selectedAnswerId != null || (selectedAnswerIds != null && !selectedAnswerIds.isEmpty()));
    }

    /** True if answered and correct. */
    public boolean isAnsweredCorrectly() {
        return isAnswered() && isCorrect;
    }

    /** True if answered and incorrect. */
    public boolean isAnsweredIncorrectly() {
        return isAnswered() && !isCorrect;
    }

    /** True if skipped or no answer selected. */
    public boolean isNotAnswered() {
        return isSkipped || (selectedAnswerId == null && (selectedAnswerIds == null || selectedAnswerIds.isEmpty()));
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
