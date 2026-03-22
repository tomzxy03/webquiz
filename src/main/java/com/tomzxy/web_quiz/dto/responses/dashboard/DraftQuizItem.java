package com.tomzxy.web_quiz.dto.responses.dashboard;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftQuizItem {
    private Long id;
    private String title;
    private String subject;
    private Integer questionCount;
    private String updatedAt; // ISO
}
