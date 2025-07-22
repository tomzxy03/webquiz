package com.tomzxy.web_quiz.models.NotificationUser;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUserId {
    private Long userId;
    private Long notificationId;
}
