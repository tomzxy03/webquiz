package com.tomzxy.web_quiz.dto.responses.dashboard;

import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResDTO {
    private UserResDTO user;
    private DashboardUserStats userStats;
    private List<InProgressQuizInstance> inProgressInstances;
    private List<QuizRecentItem> recentAndUpcoming;
    private List<DashboardGroupSummary> groups;
    private List<DraftQuizItem> draftQuizzes;
}
