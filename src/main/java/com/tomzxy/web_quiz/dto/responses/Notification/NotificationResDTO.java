package com.tomzxy.web_quiz.dto.responses.Notification;


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
    private String lobbyName;          // class id nếu type = GROUP
    private String hostName;           // id của host/admin tạo
    private LocalDateTime createdAt;
}
