package com.tomzxy.web_quiz.models.NotificationUser;

import java.time.LocalDateTime;

import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.models.Notification;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "user_notifications")
public class UserNotification {
    @EmbeddedId
    private NotificationUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("notificationId")
    private Notification notification;

    private boolean read;
    private LocalDateTime readAt;
}
