package com.tomzxy.web_quiz.dto.responses.Quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for GET /api/quiz-instances/{instanceId}/state (resume
 * endpoint).
 * Returns questions with user's saved answers and remaining time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizStateResDTO {
    private Long instanceId;
    private String status;
    private long remainingSeconds;
    private List<QuizStateQuestionDTO> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizStateQuestionDTO {
        private String questionId; // question key in snapshot
        private String content;
        private String type;
        private String answerType;
        private Integer orderIndex;
        private Long points;
        private List<QuizStateAnswerDTO> answers;
        private List<Integer> userAnswer; // user's selected indices or null
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizStateAnswerDTO {
        private Integer index; // 0-based index
        private String content;
        private String type;
    }
}
