package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByActive (PageRequest pageRequest);

    Optional<Notification> findByGroupIdAndActive(Long groupId);   // lấy theo lobby

    Optional<Notification> findByHostIdAndActive(Long hostId);     // lấy theo người tạo

}
