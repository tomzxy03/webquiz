package com.tomzxy.web_quiz.dto.responses.dashboard;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizRecentItem {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private String status; // IN_PROGRESS | DONE | UPCOMING | MISSED
    private String submittedAt; // ISO
    private String startDate; // ISO
    private String endDate; // ISO
}
