package com.tomzxy.web_quiz.models.Quiz;

import jakarta.persistence.*;

import lombok.*;
import jakarta.persistence.Index;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomzxy.web_quiz.dto.responses.AnswerSnapshot;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.AnswersSnapshot;
import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.QuestionSnapshot;
import com.tomzxy.web_quiz.models.QuizQuestion;
import com.tomzxy.web_quiz.models.QuizResult;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_attempts", indexes = {
    @Index(name = "idx_quiz_attempt_result", columnList = "quiz_result_id"),
    @Index(name = "idx_quiz_attempt_correct", columnList = "is_correct"),
    @Index(name = "idx_quiz_attempt_created_at", columnList = "created_at"),
    @Index(name = "idx_quiz_attempt_question_type", columnList = "question_type"),
    @Index(name = "idx_quiz_attempt_original_question", columnList = "original_question_id")
})
public class QuizAttempt extends BaseEntity {

    @Embedded
    private QuestionSnapshot questionSnapshot = new QuestionSnapshot();

    @Embedded
    private AnswersSnapshot answersSnapshot = new AnswersSnapshot();

    @Column(name = "selected_answer", length = 1000)
    private String selectedAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Column(name = "is_skipped", nullable = false)
    private boolean isSkipped = false;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned = 0;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_result_id", nullable = false)
    private QuizResult quizResult;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_question_id")
    private Question originalQuestion;

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
        this.selectedAnswer = null;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void setAnswer(String answer) {
        this.selectedAnswer = answer;
        this.isSkipped = false;
        this.answeredAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isAnswered() {
        return selectedAnswer != null && !selectedAnswer.trim().isEmpty();
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

    public String getStatus() {
        if (isSkipped) {
            return "SKIPPED";
        } else if (isCorrect) {
            return "CORRECT";
        } else if (isAnswered()) {
            return "INCORRECT";
        } else {
            return "NOT_ANSWERED";
        }
    }

    // === SNAPSHOT METHODS ===
    public void snapshotQuestion(Question question) {
        if (question != null) {
            if (this.questionSnapshot == null) {
                this.questionSnapshot = new QuestionSnapshot();
            }
            this.questionSnapshot.setQuestionText(question.getQuestionName());
            this.questionSnapshot.setQuestionType(question.getQuestionType().name());

        }
    }

    public void snapshotAnswers(Set<Answer> answers) {
        if (answers != null && !answers.isEmpty()) {
            try {
                List<AnswerSnapshot> answerSnapshots = answers.stream()
                    .map(answer -> AnswerSnapshot.builder()
                        .id(answer.getId())
                        .answerName(answer.getAnswerName())
                        .answerCorrect(answer.isAnswerCorrect())
                        .optionOrder(answer.getOptionOrder())
                        .optionLabel(answer.getOptionLabel())
                        .build())
                    .collect(Collectors.toList());
                
                ObjectMapper objectMapper = new ObjectMapper();
                String optionsJson = objectMapper.writeValueAsString(answerSnapshots);
                if (this.answersSnapshot == null) {
                    this.answersSnapshot = new AnswersSnapshot();
                }
                this.answersSnapshot.setAllAnswerOptions(optionsJson);
                
                String correctText = answers.stream()
                    .filter(Answer::isAnswerCorrect)
                    .map(Answer::getAnswerName)
                    .collect(Collectors.joining("; "));
                this.answersSnapshot.setCorrectAnswerText(correctText);
            } catch (Exception e) {
                String correctText = answers.stream()
                    .filter(Answer::isAnswerCorrect)
                    .map(Answer::getAnswerName)
                    .collect(Collectors.joining("; "));
                if (this.answersSnapshot == null) {
                    this.answersSnapshot = new AnswersSnapshot();
                }
                this.answersSnapshot.setCorrectAnswerText(correctText);
            }
        }
    }

    public void setUserResponse(String response, boolean isCorrect) {
        this.selectedAnswer = response;
        this.isCorrect = isCorrect;
        Integer qp = this.questionSnapshot != null ? this.questionSnapshot.getQuestionPoints() : 0;
        this.pointsEarned = isCorrect ? (qp != null ? qp : 0) : 0;
        this.answeredAt = LocalDateTime.now();
    }

    // === Compatibility getters for mapping ===
    public String getQuestionText() {
        return this.questionSnapshot != null ? this.questionSnapshot.getQuestionText() : null;
    }

    public String getQuestionType() {
        return this.questionSnapshot != null ? this.questionSnapshot.getQuestionType() : null;
    }

    public Integer getQuestionPoints() {
        return this.questionSnapshot != null ? this.questionSnapshot.getQuestionPoints() : null;
    }

    public String getCorrectAnswerText() {
        return this.answersSnapshot != null ? this.answersSnapshot.getCorrectAnswerText() : null;
    }

    public String getAllAnswerOptions() {
        return this.answersSnapshot != null ? this.answersSnapshot.getAllAnswerOptions() : null;
    }

    @Override
    public String toString() {
        String qt = getQuestionText();
        return "QuizAttempt{" +
                "id=" + getId() +
                ", questionText='" + (qt != null ? qt.substring(0, Math.min(30, qt.length())) + "..." : "null") + '\'' +
                ", isCorrect=" + isCorrect +
                ", isSkipped=" + isSkipped +
                ", pointsEarned=" + pointsEarned +
                ", status=" + getStatus() +
                '}';
    }
}
