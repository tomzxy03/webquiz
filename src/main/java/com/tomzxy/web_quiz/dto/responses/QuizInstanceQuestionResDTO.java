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
public class QuizInstanceQuestionResDTO {
    private Long id;
    private Integer displayOrder;
    private Long points;
    private String questionText;
    private String type;
    private String answerType;
    private List<QuizInstanceAnswerResDTO> answers;
}