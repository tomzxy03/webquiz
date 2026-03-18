package com.tomzxy.web_quiz.models.Host;


import com.tomzxy.web_quiz.enums.LobbyRole;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "lobby_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"lobby_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_lobby_member_user", columnList = "user_id"),
                @Index(name = "idx_lobby_member_lobby", columnList = "lobby_id"),
                @Index(name = "idx_lobby_member_lobby_user", columnList = "lobby_id,user_id")
        }
)
public class LobbyMember {
    @EmbeddedId
    private LobbyMemberId id;

    @ManyToOne
    @MapsId("lobbyId")
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    private LobbyRole role;
}
