package com.tomzxy.web_quiz.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.*;

import com.tomzxy.web_quiz.enums.NotificationType;

@Entity
@Table(name = "notifications")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_id", nullable = true)
    private Lobby lobby;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User host;
}
