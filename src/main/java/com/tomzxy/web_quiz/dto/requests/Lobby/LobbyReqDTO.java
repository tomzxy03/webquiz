package com.tomzxy.web_quiz.dto.requests.Lobby;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LobbyReqDTO {
    @NotBlank(message = "Lobby name is required")
    private String lobbyName;

    @NotBlank(message = "Host name is required")
    private String hostName;
}
