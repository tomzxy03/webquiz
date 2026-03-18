package com.tomzxy.web_quiz.dto.responses.Quiz;

import com.tomzxy.web_quiz.models.Quiz.QuizConfig;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizDetailResDTO {

    private QuizResDTO quiz;

    private QuizConfig quizConfig;
    // e.g. "IN_PROGRESS", "NONE"
    private String attemptState;
    private Long instanceId;
    private int totalAttempt;
}
