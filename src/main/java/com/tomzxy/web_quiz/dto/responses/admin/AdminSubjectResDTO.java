package com.tomzxy.web_quiz.dto.responses.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSubjectResDTO {
    private Long id;
    private String subjectName;
    private String description;
    private Integer quizCount;
    private String createdAt;
}
