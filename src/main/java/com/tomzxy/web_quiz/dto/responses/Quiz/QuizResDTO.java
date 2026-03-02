package com.tomzxy.web_quiz.dto.responses.Quiz;

import com.tomzxy.web_quiz.enums.QuizStatus;
import com.tomzxy.web_quiz.enums.QuizVisibility;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResDTO {
    private Long id;
    private String title;
    private String description;
    private int totalQuestion;
    private QuizVisibility quizVisibility;
    private QuizStatus status;
    private String hostName;
    private String lobbyName;
} 