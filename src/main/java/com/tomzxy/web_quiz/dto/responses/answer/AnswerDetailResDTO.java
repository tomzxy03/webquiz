package com.tomzxy.web_quiz.dto.responses.answer;


import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AnswerDetailResDTO {
    private Long id;

    private String answerText;

    private QuestionAndAnswerType answerType;

    private String isActive;

    private Integer orderIndex; // dùng cho shuffle / snapshot

    private boolean answerCorrect;

}
