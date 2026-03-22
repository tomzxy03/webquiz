package com.tomzxy.web_quiz.dto.responses.admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResDTO {

    private AdminDashboardCounts counts;
    private List<AdminDashboardTrendPoint> trends;
    private List<AdminDashboardRecentItem> recent;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminDashboardCounts {
        private Long totalUsers;
        private Long totalGroups;
        private Long totalQuizzes;
        private Long totalResults;
        private Long activeQuizzes;
        private Long newUsers7d;
        private Long newGroups7d;
        private Long newQuizzes7d;
        private Long newResults7d;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminDashboardTrendPoint {
        private String date;
        private Long users;
        private Long groups;
        private Long quizzes;
        private Long results;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminDashboardRecentItem {
        private Long id;
        private String type;
        private String title;
        private String subtitle;
        private String createdAt;
        private String status;
    }
}
