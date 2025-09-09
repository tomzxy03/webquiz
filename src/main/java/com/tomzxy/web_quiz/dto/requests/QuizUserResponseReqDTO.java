package com.tomzxy.web_quiz.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUserResponseReqDTO {

    @NotNull(message = "Quiz instance ID is required")
    private Long quizInstanceId;

    private Long selectedAnswerId;
    
    private String selectedAnswerText;
    
    private Boolean isCorrect;
    
    private Integer pointsEarned;
    
    private Integer responseTimeSeconds;
    
    private LocalDateTime answeredAt;
    
    private Boolean isSkipped;
    
    private Long questionSnapshotId;
}
