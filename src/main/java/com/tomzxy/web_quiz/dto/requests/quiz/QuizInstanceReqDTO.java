package com.tomzxy.web_quiz.dto.requests.quiz;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizInstanceReqDTO {
    @NotNull(message = "Quiz ID is required")
    private Long quizId;
} 