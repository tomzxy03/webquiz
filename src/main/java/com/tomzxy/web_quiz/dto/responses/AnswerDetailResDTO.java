package com.tomzxy.web_quiz.dto.responses;


import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerDetailResDTO {

    String answer_name;

    QuestionAndAnswerType answer_type;

    boolean is_correct;

}
