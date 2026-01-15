package com.tomzxy.web_quiz.dto.responses.Quiz;

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
    private QuizType quizType;
    private String hostName;
    private String lobbyName;
} 