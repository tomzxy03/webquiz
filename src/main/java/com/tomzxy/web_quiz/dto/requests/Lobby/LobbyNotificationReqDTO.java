package com.tomzxy.web_quiz.dto.requests.Lobby;


import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class LobbyNotificationReqDTO {
    @NotBlank(message = "Lobby ID is required")
    private Long lobbyId;

    private NotificationReqDTO notificationReqDTO;

}
