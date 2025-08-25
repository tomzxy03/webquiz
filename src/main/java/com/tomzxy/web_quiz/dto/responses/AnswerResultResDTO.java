package com.tomzxy.web_quiz.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResultResDTO {
    
    private Long answerInstanceId;
    private Integer displayOrder;
    private String answerText;
    private String optionLabel;
    private boolean isCorrect;
    private boolean isUserSelected;
} 