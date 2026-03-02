package com.tomzxy.web_quiz.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for creating a new exam attempt.
 * Matches API_JSON.md specification for POST /attempts endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttemptReqDTO {
    private Long quizId;
    private List<AnswerSubmissionDTO> answers;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
