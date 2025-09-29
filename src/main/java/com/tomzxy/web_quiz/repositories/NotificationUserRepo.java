package com.tomzxy.web_quiz.repositories;


import com.tomzxy.web_quiz.models.NotificationUser.NotificationUserId;
import com.tomzxy.web_quiz.models.NotificationUser.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationUserRepo extends JpaRepository<UserNotification, NotificationUserId> {

    Page<UserNotification> findByIdUserId(Long userId, Pageable pageable);

    Optional<UserNotification> findByIdUserIdAndIsReadFalse(Long userId);

    boolean existsByIdUserIdAndIdNotificationId(Long userId, Long notificationId);

    @Modifying
    @Query("UPDATE UserNotification un SET un.isRead = true, un.readAt = CURRENT_TIMESTAMP " +
            "WHERE un.id.userId = :userId AND un.isRead = false")
    int markAllAsReadByUser(@Param("userId") Long userId);
}

