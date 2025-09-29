package com.tomzxy.web_quiz.dto.requests.Notification;


import lombok.Getter;

@Getter
public class NotificationUserReqDTO {
    private Long notificationId;
    private Long userId;
    private Boolean isRead;
}
