package com.tomzxy.web_quiz.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSnapshot {
    private Long id;
    private String answerName;
    private boolean answerCorrect;
    private Integer optionOrder;
    private String optionLabel;
} 