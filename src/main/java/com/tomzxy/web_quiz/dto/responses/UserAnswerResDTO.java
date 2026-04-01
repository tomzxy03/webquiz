package com.tomzxy.web_quiz.dto.responses;

import com.tomzxy.web_quiz.dto.responses.answer.AnswerResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionResDTO;
import com.tomzxy.web_quiz.enums.AnswerType;
import com.tomzxy.web_quiz.enums.ContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for user answer in attempt detail.
 * Matches API_JSON.md specification for answers array in attempt detail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerResDTO {
    private Long id;
    private Long questionId;

    private String questionName;
    private ContentType type;
    private AnswerType answerType;

    private List<AnswerResDTO> options; 
    private List<Long> selectedOptionIds;

    private String answerText;

    private Boolean isCorrect;
    private Long pointsEarned;
}
