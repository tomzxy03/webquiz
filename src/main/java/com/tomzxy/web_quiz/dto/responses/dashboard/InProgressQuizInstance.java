package com.tomzxy.web_quiz.dto.responses.dashboard;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InProgressQuizInstance {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Integer answeredCount;
    private Integer totalQuestions;
    private Long timeRemainingSeconds;
    private String resumedAt; // ISO
}
