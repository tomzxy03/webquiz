package com.tomzxy.web_quiz.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizInstanceAnswerResDTO {
    
    private Long id;
    private Integer displayOrder;
    private String answerText;
    private String optionLabel; // A, B, C, D...
} 