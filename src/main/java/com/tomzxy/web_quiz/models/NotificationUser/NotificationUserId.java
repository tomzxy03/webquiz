package com.tomzxy.web_quiz.models.NotificationUser;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationUserId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "notification_id")
    private Long notificationId;
}
