package com.tomzxy.web_quiz.dto.responses.lobby;

import java.util.Set;

import com.tomzxy.web_quiz.dto.responses.user.UserLobbyResDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LobbyMemberResDTO {
    private Long id;
    private Set<UserLobbyResDTO> members;
}
