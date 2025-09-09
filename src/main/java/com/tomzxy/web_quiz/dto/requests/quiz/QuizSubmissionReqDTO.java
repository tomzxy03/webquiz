package com.tomzxy.web_quiz.dto.requests.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionReqDTO {
    
    private Long quizInstanceId;
    private Long userId;
    private Map<Long, String> answers; // questionInstanceId -> selectedAnswerText
    private Map<Long, Long> answerIds; // questionInstanceId -> selectedAnswerId
    private Integer totalTimeSpentSeconds;
    private LocalDateTime submittedAt;
} 
