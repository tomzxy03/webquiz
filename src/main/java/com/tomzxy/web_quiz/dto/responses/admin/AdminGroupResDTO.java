package com.tomzxy.web_quiz.dto.responses.admin;

import com.tomzxy.web_quiz.enums.AdminGroupStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminGroupResDTO {
    private Long id;
    private String name;
    private String ownerName;
    private Integer memberCount;
    private Integer quizCount;
    private AdminGroupStatus status;
    private String createdAt;
}
