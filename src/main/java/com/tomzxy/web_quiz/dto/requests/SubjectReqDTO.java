package com.tomzxy.web_quiz.dto.requests;


import com.tomzxy.web_quiz.enums.Gender;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
public class SubjectReqDTO {
    @NotBlank(message = "Subject name not blank")
    String subjectName;

    @NotBlank(message = "Subject description not blank")
    String description;

}
