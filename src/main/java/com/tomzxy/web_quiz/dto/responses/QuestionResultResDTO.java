package com.tomzxy.web_quiz.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultResDTO {
    
    private Long questionInstanceId;
    private Integer displayOrder;
    private String questionText;
    private String questionType;
    private Integer points;
    private Integer earnedPoints;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;
    private boolean isSkipped;
    private String status;
    private List<AnswerResultResDTO> allAnswers;
} 