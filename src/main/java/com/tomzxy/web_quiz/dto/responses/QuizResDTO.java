package com.tomzxy.web_quiz.dto.responses;

import com.tomzxy.web_quiz.enums.QuizType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;


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
    private String groupName;
    private List<QuizQuestionResDTO> questions;
    private List<QuizResultResDTO> submissions;
} 