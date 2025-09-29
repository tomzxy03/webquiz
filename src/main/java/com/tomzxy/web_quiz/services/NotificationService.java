package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.responses.NotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;

public interface NotificationService {
    PageResDTO<?> getAllNotification(int page, int size);

    NotificationResDTO getNotificationId (Long notificationId);

    NotificationResDTO createNotification(NotificationReqDTO notificationReqDTO);

    NotificationResDTO updateNotification(Long notificationId, NotificationReqDTO notificationReqDTO);

    Void deleteNotification(Long notificationId);
}
