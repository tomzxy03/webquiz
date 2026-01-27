package com.tomzxy.web_quiz.models.snapshot;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionSnapshot {
    private Long quizId;
    private String shuffleSeed;
    private Integer version;            // snapshot schema version
    private LocalDateTime generatedAt;   // thời điểm tạo snapshot
    private Boolean shuffledQuestions;
    private Boolean shuffledAnswers;

    private List<QuestionSnapshot> questions = new ArrayList<>();
}
