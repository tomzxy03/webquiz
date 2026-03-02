package com.tomzxy.web_quiz.models.snapshot;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSnapshot extends BaseEntity {
    private String key;              // "Q1", UUID, hoặc hash
    private String content;          // nội dung câu hỏi
    @Enumerated(EnumType.STRING)
    private QuestionAndAnswerType type;             // TEXT, IMAGE, ...
    private Integer orderIndex;
    private Integer points;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_snapshot_id")
    private QuizQuestionSnapshot quizSnapshot;

    @OneToMany(
            mappedBy = "questionSnapshot",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AnswerSnapshot> answers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "question_snapshot_correct_answers",
            joinColumns = @JoinColumn(name = "question_snapshot_id")
    )
    @Column(name = "correct_answer_id")
    private List<String> correctAnswerIds = new ArrayList<>();
}
