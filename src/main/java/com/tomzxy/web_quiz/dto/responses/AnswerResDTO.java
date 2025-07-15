package com.tomzxy.web_quiz.dto.responses;


import com.tomzxy.web_quiz.enums.Gender;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerResDTO {
    Long id;

    String answerName;

    QuestionAndAnswerType answerType;

    boolean isCorrect;

    boolean is_active;
}
