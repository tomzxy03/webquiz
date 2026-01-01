package com.tomzxy.web_quiz.dto.responses;

import com.tomzxy.web_quiz.dto.responses.question.QuestionSnapshotResDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUserResponseResDTO {

    private Long id;
    private Long quizInstanceId;
    private Long selectedAnswerId;
    private String selectedAnswerText;
    private Boolean isCorrect;
    private Integer pointsEarned;
    private Integer responseTimeSeconds;
    private LocalDateTime answeredAt;
    private Boolean isSkipped;
    private String status;
    private List<QuestionSnapshotResDTO> questionSnapshots;
    
    // Computed fields
    private Boolean isAnswered;
    private Boolean isNotAnswered;
    private Boolean isAnsweredCorrectly;
    private Boolean isAnsweredIncorrectly;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
