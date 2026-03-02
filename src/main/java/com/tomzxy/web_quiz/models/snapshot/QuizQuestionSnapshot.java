package com.tomzxy.web_quiz.models.snapshot;


import com.tomzxy.web_quiz.models.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionSnapshot extends BaseEntity {
    private Long quizId;
    private String shuffleSeed;
    private Integer version;            // snapshot schema version
    private LocalDateTime generatedAt;   // thời điểm tạo snapshot
    private Integer totalPoints;
    private Boolean shuffledQuestions;
    private Boolean shuffledAnswers;
    @OneToMany(
            mappedBy = "quizSnapshot",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionSnapshot> questions;
}
