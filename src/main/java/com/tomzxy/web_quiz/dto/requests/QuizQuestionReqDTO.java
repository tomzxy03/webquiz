package com.tomzxy.web_quiz.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import java.util.Set;

@Getter
public class QuizQuestionReqDTO {
    @NotBlank(message = "Question text is required")
    private String questionText;
    
    @NotNull(message = "Is custom flag is required")
    private Boolean isCustom;
    
    private String customQuestion; // nullable when isCustom is false
    
    private Long questionId; // nullable when isCustom is true
    
    @NotEmpty(message = "Quiz answers are required")
    private Set<QuizAnswerReqDTO> quizAnswers;
} 