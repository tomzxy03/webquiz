package com.tomzxy.web_quiz.dto.responses.question;

import com.tomzxy.web_quiz.dto.responses.answer.AnswerFileResDTO;
import com.tomzxy.web_quiz.enums.AnswerType;
import com.tomzxy.web_quiz.enums.ContentType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionFileResDTO {

    private Long id;

    private String questionName;

    private ContentType type;

    private AnswerType answerType;

    private List<AnswerFileResDTO> answers;
}
