package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
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
    @Column(name = "level", nullable = true, length = 20)
    private Level level;

    // Thuộc về Question Bank nào (REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = true)
    private QuestionBank bank;

    // Thuộc về Folder nào (OPTIONAL - null nếu là question riêng lẻ)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder; // Có thể null!

    @Column(name = "content_hash", nullable = false, unique = true, length = 64)
    private String contentHash; // to distinguish, comparing between question and question

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers = new HashSet<>();

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

    @Override
    public String toString() {
        return "Question{" +
                "id=" + getId() +
                ", questionName='"
                + (questionName != null ? questionName.substring(0, Math.min(50, questionName.length())) + "..."
                        : "null")
                + '\'' +
                ", questionType=" + questionType +
                ", level=" + level +
                ", isActive=" + isActive() +
                '}';
    }
}
