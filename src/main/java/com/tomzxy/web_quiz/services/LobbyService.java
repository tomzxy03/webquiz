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

    // Members
    PageResDTO<?> getAllMembers(Long lobbyId, int page, int size);

    LobbyResDTO addMember(Long lobbyId, Long userId);

    void removeMember(Long lobbyId, Long userId);

    // Notifications / Announcements
    PageResDTO<?> getAllNotifications(Long lobbyId);

    // User's groups
    PageResDTO<?> getLobbyByUser(Long userId, int page, int size);

    // Quizzes within group
    PageResDTO<?> getGroupQuizzes(Long lobbyId, int page, int size);

    LobbyResDTO addQuizToGroup(Long lobbyId, Long quizId);

    void removeQuizFromGroup(Long lobbyId, Long quizId);
}
