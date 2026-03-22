package com.tomzxy.web_quiz.dto.responses.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LobbyCodeInviteResDTO {
    private Long lobbyId;
    private String codeInvite;
}
