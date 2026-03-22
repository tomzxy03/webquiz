package com.tomzxy.web_quiz.dto.requests.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSubjectReqDTO {
    private String subjectName;
    private String description;
}
