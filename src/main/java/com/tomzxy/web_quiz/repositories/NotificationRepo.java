package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.NotificationUser.Notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByIsActiveTrue (PageRequest pageRequest);

    Page<Notification> findByLobbyIdAndIsActiveTrue(Long lobbyId, Pageable pageable);   // lấy theo lobby

    Optional<Notification> findByHostIdAndIsActiveTrue(Long hostId);     // lấy theo người tạo

}
