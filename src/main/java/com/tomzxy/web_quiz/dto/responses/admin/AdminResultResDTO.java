package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminResultStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResultResDTO {
    private Long id;
    private String quizTitle;
    private String userName;
    private String groupName;
    private AdminResultStatus status;
    private Double score;
    private String submittedAt;
    private Integer durationSeconds;
}
