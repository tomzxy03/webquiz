package com.tomzxy.web_quiz.dto.responses.dashboard;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardUserStats {
    private Integer totalQuizzesTaken;
    private Integer inProgressCount;
    private Integer completedCount;
}
