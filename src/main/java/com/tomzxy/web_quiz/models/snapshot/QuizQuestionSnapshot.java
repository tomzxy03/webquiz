package com.tomzxy.web_quiz.models.snapshot;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class QuizQuestionSnapshot {
    private Long quizId;
    private String shuffleSeed;
    private Integer version; // snapshot schema version
    private LocalDateTime generatedAt; // thời điểm tạo snapshot
    private Long totalPoints;
    private Integer duration; // quiz duration in minutes
    private Boolean shuffledQuestions;
    private Boolean shuffledAnswers;
    private List<QuestionSnapshot> questions;
}
