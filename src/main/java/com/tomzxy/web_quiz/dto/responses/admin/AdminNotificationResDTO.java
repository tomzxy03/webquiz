package com.tomzxy.web_quiz.dto.responses.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationResDTO {
    private Long id;
    private String title;
    private String content;
    private String groupName;
    private String createdAt;
}
