package com.tomzxy.web_quiz.dto.responses.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminQuestionBankResDTO {
    private Long id;
    private String ownerName;
    private Integer folderCount;
    private Integer questionCount;
    private String createdAt;
}
