package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyCodeInviteResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyNotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyQuizResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionResDTO;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface LobbyService {
    LobbyResDTO createLobby(LobbyReqDTO lobbyReqDTO);

    PageResDTO<?> getAllLobby(int page, int size);

    PageResDTO<?> getAllLobbyOwned(int page, int size);

    PageResDTO<?> getAllLobbyJoined(int page, int size);

    LobbyResDTO getLobby(Long lobbyId);

    LobbyResDTO updateLobby(Long lobbyId, LobbyReqDTO lobbyReqDTO);

    void deleteLobby(Long lobbyId);

    // Members
    PageResDTO<?> getAllMembers(Long lobbyId, int page, int size);

    LobbyResDTO addMember(Long lobbyId, Long userId);

    void removeMember(Long lobbyId, Long userId);


    void leaveLobby(Long lobbyId);

    PageResDTO<?> getAllNotifications(Long lobbyId, int page, int size);

    LobbyNotificationResDTO addNotification(Long lobbyId, NotificationReqDTO notificationReqDTO);

    @Transactional
    LobbyNotificationResDTO updateNotification(Long lobbyId, Long notificationId, NotificationReqDTO notificationReqDTO);

    void deleteNotification(Long lobbyId, Long notificationId);

    LobbyResDTO joinLobby(Long lobbyId);

    // User's groups
    PageResDTO<?> getLobbyByUser(Long userId, int page, int size);

    // Quizzes within group
    PageResDTO<QuizResDTO> getGroupQuizzes(Long lobbyId, int page, int size);
    PageResDTO<QuizResDTO> getGroupQuizzesOpened(Long lobbyId, int page, int size);

    LobbyQuizResDTO addQuizToGroup(Long lobbyId, QuizReqDTO quizReqDTO);

    @Transactional
    LobbyQuizResDTO updateQuizInGroup(Long lobbyId, Long quizId, QuizReqDTO quizReqDTO);

    void removeQuizFromGroup(Long lobbyId, Long quizId);

    //get code invite
    LobbyCodeInviteResDTO getCodeInvite(Long lobbyId);

    // find
    LobbyResDTO findLobbyByCode(String code);

    // reload codeInvite
    LobbyCodeInviteResDTO reloadCodeInvite(Long lobbyId);
}
