package com.tomzxy.web_quiz.dto.requests.Lobby;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyReqDTO {
    @NotBlank(message = "Lobby name is required")
    private String lobbyName;
}
