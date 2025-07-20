package com.tomzxy.web_quiz.dto.responses;


import com.tomzxy.web_quiz.dto.responses.answer.AnswerDetailResDTO;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;
import java.util.List;
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

    boolean is_active;

    Set<AnswerDetailResDTO> answers;
}
