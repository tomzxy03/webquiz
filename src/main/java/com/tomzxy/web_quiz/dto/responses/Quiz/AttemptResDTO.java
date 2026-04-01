package com.tomzxy.web_quiz.dto.responses.Quiz;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResDTO {
    private Long id;
    private Long quizId;
    private Long userId;
    private String title;
    private Double score; // Percentage as double (e.g., 85.0 for 85%)
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Long points;
    private Long duration; // Duration in milliseconds
    private LocalDateTime completedAt;
    private List<String> badges;
    private List<String> badgeColors;
}
