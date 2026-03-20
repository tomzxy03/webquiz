package com.tomzxy.web_quiz.dto.responses.question;


import com.tomzxy.web_quiz.dto.responses.answer.AnswerResDTO;
import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResDTO {

    private Long id;

    private String questionName;

    private QuestionAndAnswerType questionType;

    private List<AnswerResDTO> answers;
}

