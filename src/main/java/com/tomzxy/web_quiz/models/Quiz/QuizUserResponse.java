package com.tomzxy.web_quiz.models.Quiz;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.tomzxy.web_quiz.enums.ResponseStatus;
import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.snapshot.QuestionSnapshot;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_user_responses", indexes = {
    @Index(name = "idx_quiz_user_response_instance", columnList = "quiz_instance_id"),
    @Index(name = "idx_quiz_user_response_question", columnList = "quiz_instance_question_id"),
    @Index(name = "idx_quiz_user_response_correct", columnList = "is_correct")
})
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

    @ManyToMany
    @JoinTable(name = "quiz_user_responses_question_snapshots",
        joinColumns = @JoinColumn(name = "quiz_user_response_id"),
        inverseJoinColumns = @JoinColumn(name = "question_snapshot_id")
    )
    private List<QuestionSnapshot> questionSnapshot;

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
        this.selectedAnswerId = null;
        this.selectedAnswerText = null;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void setAnswer(Long answerId, String answerText, boolean isCorrect) {
        // Validation
        if (answerId == null && (answerText == null || answerText.trim().isEmpty())) {
            throw new IllegalArgumentException("Either answerId or answerText must be provided");
        }
        
        this.selectedAnswerId = answerId;
        this.selectedAnswerText = answerText;
        this.isCorrect = isCorrect;
        this.isSkipped = false;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isAnswered() {
        return selectedAnswerId != null && !isSkipped;
    }

    public boolean isNotAnswered() {
        return !isAnswered() && !isSkipped;
    }

    public boolean isAnsweredCorrectly() {
        return isCorrect && !isSkipped;
    }

    public boolean isAnsweredIncorrectly() {
        return !isCorrect && !isSkipped && isAnswered();
    }

    public ResponseStatus getStatus() {
        if (isSkipped) {
            return ResponseStatus.SKIPPED;
        } else if (isCorrect) {
            return ResponseStatus.CORRECT;
        } else if (isAnswered()) {
            return ResponseStatus.INCORRECT;
        } else {
            return ResponseStatus.NOT_ANSWERED;
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