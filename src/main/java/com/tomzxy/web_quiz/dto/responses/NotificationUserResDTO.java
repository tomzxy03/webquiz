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
public class NotificationUserResDTO {
    private Long userId;
    private Long notificationId;
    private boolean isRead;
    private LocalDateTime readAt;

    // embed notification info
    private NotificationResDTO notificationResDTO;
}
