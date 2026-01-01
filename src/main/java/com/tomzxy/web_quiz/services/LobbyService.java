package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;

public interface LobbyService {
    LobbyResDTO createLobby(LobbyReqDTO lobbyReqDTO);
    PageResDTO<?> getAllLobby(int page, int size);
    LobbyResDTO getLobby(Long lobbyId);
    LobbyResDTO updateLobby(Long lobbyId, LobbyReqDTO lobbyReqDTO);
    void deleteLobby(Long lobbyId);
    PageResDTO<?> getAllMembers(Long lobbyId, int page, int size);
    PageResDTO<?> getAllNotifications(Long lobbyId);


}
