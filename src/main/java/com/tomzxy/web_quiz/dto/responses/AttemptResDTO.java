package com.tomzxy.web_quiz.dto.responses;

import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for attempt list endpoints.
 * Matches API_JSON.md specification for /attempts endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResDTO {
    private Long id;
    private Long quizId;
    private Long userId;
    private QuizResDTO quiz; // Optional quiz object
    private String title;
    private String date;
    private String score; // Percentage as string (e.g., "85%")
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Long points;
    private String duration; // Formatted duration string
    private LocalDateTime completedAt;
    private List<String> badges;
    private List<String> badgeColors;
}
