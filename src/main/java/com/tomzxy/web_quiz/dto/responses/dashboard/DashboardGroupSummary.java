package com.tomzxy.web_quiz.dto.responses.dashboard;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardGroupSummary {
    private Long id;
    private String name;
    private String role; // OWNER | HOST | ADMIN | MEMBER
    private Integer openQuizzesCount;
}
