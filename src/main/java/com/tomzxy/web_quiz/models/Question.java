package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.Host.QuestionFolder;
import com.tomzxy.web_quiz.models.User.User;
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
        @Index(name = "idx_question_type", columnList = "question_type"),
        @Index(name = "idx_question_level", columnList = "level"),
        @Index(name = "idx_question_created_at", columnList = "created_at"),
        @Index(name = "idx_question_bank", columnList = "bank_id"),
        @Index(name = "idx_question_folder", columnList = "folder_id"),
        @Index(name = "idx_question_bank_folder", columnList = "bank_id, folder_id")
})
public class Question extends BaseEntity {

    @Column(name = "question_name", nullable = false, length = 1000)
    private String questionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private QuestionAndAnswerType questionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private Level level;

    // Thuộc về Question Bank nào (REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private QuestionBank bank;

    // Thuộc về Folder nào (OPTIONAL - null nếu là question riêng lẻ)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private QuestionFolder folder;  // Có thể null!


    @Column(name = "content_hash", nullable = false,unique = true,length = 64)
    private String contentHash; // to distinguish, comparing between question and question

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Answer> answers = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<QuestionBank> hostQuestions = new HashSet<>();

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



    public boolean isAvailable() {
        return isActive();
    }

    public int getAnswerCount() {
        return answers.size();
    }

    public boolean hasAnswers() {
        return !answers.isEmpty();
    }

//    public boolean hasExplanation() {
//        return description != null && !description.trim().isEmpty();
//    }
//    // Enhanced Analytics Methods
//    public int getTotalUsage() {
//        return quizQuestions.size();
//    }
//
//    public boolean isFrequentlyUsed() {
//        return getTotalUsage() >= 5; // Define threshold for frequently used
//    }
//
//    public boolean isRarelyUsed() {
//        return getTotalUsage() <= 1;
//    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + getId() +
                ", questionName='" + (questionName != null ? questionName.substring(0, Math.min(50, questionName.length())) + "..." : "null") + '\'' +
                ", questionType=" + questionType +
                ", level=" + level +
                ", isActive=" + isActive() +
                '}';
    }
}
