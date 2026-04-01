package com.tomzxy.web_quiz.dto.responses.question;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionResDTO {
    private QuestionResDTO question;
    private List<Long> correctAnswerIds;
}
