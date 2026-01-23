package com.tomzxy.web_quiz.dto.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SubjectReqDTO {
    @NotBlank(message = "Subject name not blank")
    String subjectName;

    @NotBlank(message = "Subject description not blank")
    String description;

}
