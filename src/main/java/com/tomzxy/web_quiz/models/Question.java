package com.tomzxy.web_quiz.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.Index;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Table(name = "questions", indexes = {
    @Index(name = "idx_question_subject", columnList = "subject_id"),
    @Index(name = "idx_question_type", columnList = "question_type"),
    @Index(name = "idx_question_level", columnList = "level"),
    @Index(name = "idx_question_created_at", columnList = "created_at")
})
public class Question extends BaseEntity {

    @Column(name = "question_name", nullable = false, length = 1000)
    private String questionName;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private QuestionAndAnswerType questionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private Level level;

    @Column(name = "points", nullable = false)
    private Integer points = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = true)
    @JsonIgnore
    private Subject subject;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Answer> answers = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<QuizQuestion> quizQuestions = new HashSet<>();

    // Business Logic Methods
    public void addAnswer(Answer answer) {
        if (answer != null) {
            answers.add(answer);
            answer.setQuestion(this);
        }
    }


    public void addAnswers(Collection<Answer> answersList) {
        if (answersList != null && !answersList.isEmpty()) {
            for (Answer answer : answersList) {
                addAnswer(answer);
            }
        }
    }
    
    public void removeAnswer(Answer answer) {
        if (answer != null && answers.remove(answer)) {
            answer.setQuestion(null);
        }
    }

    public Answer getCorrectAnswer() {
        return answers.stream()
                .filter(Answer::isImageAnswer)
                .findFirst()
                .orElse(null);
    }

    public Set<Answer> getCorrectAnswers() {
        return answers.stream()
                .filter(Answer::isAnswerCorrect)
                .collect(java.util.stream.Collectors.toSet());
    }

    public boolean hasMultipleCorrectAnswers() {
        return getCorrectAnswers().size() > 1;
    }

    public boolean isTextQuestion() {
        return questionType == QuestionAndAnswerType.TEXT;
    }

    public boolean isImageQuestion() {
        return questionType == QuestionAndAnswerType.IMAGE;
    }

    public boolean validateAnswer(String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        Set<Answer> correctAnswers = getCorrectAnswers();
        if (correctAnswers.isEmpty()) {
            return false;
        }

        // For now, check if user selected one of the correct answers
        return correctAnswers.stream()
                .anyMatch(answer -> answer.getId().toString().equals(userAnswer.trim()));
    }
    public boolean isEasy() {
        return level == Level.EASY;
    }

    public boolean isMedium() {
        return level == Level.MEDIUM;
    }

    public boolean isHard() {
        return level == Level.HARD;
    }

    public void activate() {
        this.setActive(true);
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void deactivate() {
        this.setActive(false);
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public boolean isAvailable() {
        return isActive();
    }

    public int getAnswerCount() {
        return answers.size();
    }

    public boolean hasAnswers() {
        return !answers.isEmpty();
    }

    public boolean hasExplanation() {
        return description != null && !description.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + getId() +
                ", questionName='" + (questionName != null ? questionName.substring(0, Math.min(50, questionName.length())) + "..." : "null") + '\'' +
                ", questionType=" + questionType +
                ", level=" + level +
                ", points=" + points +
                ", isActive=" + isActive() +
                '}';
    }
}
