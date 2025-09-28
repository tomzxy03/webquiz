package com.tomzxy.web_quiz.dto.responses;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResDTO {
    private Long id;
    private String title;
    private String content;
    private String type;           // từ enum NotificationType
    private Long groupId;          // lobby id nếu type = GROUP
    private Long hostId;           // id của host/admin tạo
    private LocalDateTime createdAt;
}
