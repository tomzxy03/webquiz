package com.tomzxy.web_quiz.dto.requests.Lobby;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class LobbyMemberReqDTO { // add list member to lobby
    @NotBlank(message = "Lobby ID is required")
    private Long lobbyId;

    @NotEmpty(message = "Member ID is required")
    private List<Long> memberIds;
}
