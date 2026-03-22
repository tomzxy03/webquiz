package com.tomzxy.web_quiz.dto.responses.answer;


import com.tomzxy.web_quiz.enums.ContentType;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class AnswerResDTO {

    private Long id;

    private String answerText;

    private ContentType answerType;

    private Integer orderIndex; // dùng cho shuffle / snapshot
}
