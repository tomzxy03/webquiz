package com.tomzxy.web_quiz.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;


@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizAttemptResDTO {
    private Long id;
    private String questionText;
    private String selectedAnswer;
    private boolean isCorrect;
} 