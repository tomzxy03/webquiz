package com.tomzxy.web_quiz.dto.requests.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tomzxy.web_quiz.models.QuizConfig;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizInstanceReqDTO {
    @NotNull(message = "Quiz ID is required")
    private Long quizId;
} 