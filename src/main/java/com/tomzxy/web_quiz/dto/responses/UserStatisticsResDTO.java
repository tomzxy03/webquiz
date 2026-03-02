package com.tomzxy.web_quiz.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for user statistics endpoint.
 * Matches API_JSON.md specification for /attempts/user/{userId}/statistics endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResDTO {
    private Long userId;
    private Integer totalQuizzesTaken;
    private Integer totalPoints;
    private Double averageScore;
    private Integer totalTimeSpent; // in minutes
    private Map<String, Integer> quizzesBySubject;
    private Map<String, Integer> quizzesByDifficulty;
    private List<AttemptResDTO> recentActivity;
}
