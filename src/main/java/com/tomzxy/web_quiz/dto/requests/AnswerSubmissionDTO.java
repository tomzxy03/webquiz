package com.tomzxy.web_quiz.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for answer submission in create attempt request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmissionDTO {
    private Long questionId;
    private List<Long> selectedOptionIds;
    private String answerText; // Optional for text-based answers
}
