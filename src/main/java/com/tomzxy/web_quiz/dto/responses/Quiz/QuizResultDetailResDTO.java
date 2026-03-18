package com.tomzxy.web_quiz.dto.responses.Quiz;

import com.tomzxy.web_quiz.dto.responses.question.QuestionResultResDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for quiz submission result and get-result API.
 * Aligned with {@link com.tomzxy.web_quiz.models.QuizUser.QuizInstance} and its relationships.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDetailResDTO {

    private Long quizInstanceId;
    private Long quizId;
    private String quizTitle;
    private Long userId;
    private String userName;
    private String status;
    private Double scorePercentage;
    private Long totalTimeSpentMinutes;
    private Long totalPoints;
    private Long earnedPoints;
    private List<QuestionResultResDTO> questionResults;
}
