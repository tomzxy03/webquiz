package com.tomzxy.web_quiz.dto.requests;


import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class QuestionReqDTO {
    @NotBlank(message = "Question name not blank")
    String question_name;

    @EnumValidate(name = "level", regex = "EASY|MEDIUM|HARD")
    Level level;
    @EnumValidate(name = "answer_type", regex = "TEXT|IMAGE")
    QuestionAndAnswerType answer_type;

    List<AnswerReqDTO> answerReqDTOList;

}
