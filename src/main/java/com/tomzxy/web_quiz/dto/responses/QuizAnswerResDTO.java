package com.tomzxy.web_quiz.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;



@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizAnswerResDTO {
    private Long id;
    private boolean isCustom;
    private boolean isCorrect;
    private String customAnswer;
    private Long answerId; // nullable
    private String answerName; // nullable
} 