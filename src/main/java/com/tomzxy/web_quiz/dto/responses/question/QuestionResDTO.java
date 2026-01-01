package com.tomzxy.web_quiz.dto.responses.question;


import com.tomzxy.web_quiz.dto.responses.AnswerResDTO;
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
public class QuestionResDTO {
    Long id;

    String questionName;

    QuestionAndAnswerType questionType;

    boolean isActive;

    Integer points;


    Set<AnswerResDTO> answers;
}
