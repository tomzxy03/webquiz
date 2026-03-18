package com.tomzxy.web_quiz.dto.responses;

import com.tomzxy.web_quiz.dto.responses.question.QuestionResDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for user answer in attempt detail.
 * Matches API_JSON.md specification for answers array in attempt detail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerResDTO {
    private Long id;
    private Long attemptId;
    private Long questionId;
    private QuestionResDTO question; // Optional question object
    private List<Long> selectedOptionIds;
    private String answerText;
    private Boolean isCorrect;
    private Long pointsEarned;
}
