package com.tomzxy.web_quiz.dto.requests.quiz;

import com.tomzxy.web_quiz.enums.QuizOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizInstanceReqDTO {
    
    private Long quizId;
    private Long userId;
    private Set<QuizOptions> options;
    private Integer timeLimitMinutes;
    private boolean shuffleQuestions = false;
    private boolean shuffleAnswers = false;
    private boolean showCorrectAnswers = false;
    private boolean showScoreImmediately = false;
    private boolean allowReview = false;
    private boolean strictTimeLimit = false;
} 