package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyMemberResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyNotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserLobbyResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.LobbyMapper;
import com.tomzxy.web_quiz.mapstructs.Notification.NotificationMapper;
import com.tomzxy.web_quiz.mapstructs.Notification.NotificationUserMapper;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.NotificationUser.Notification;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.LobbyRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LobbyServiceImpl implements LobbyService {

    private LobbyRepo lobbyRepo;
    private LobbyMapper lobbyMapper;
    private ConvertToPageResDTO convertToPageResDTO;
    private UserMapper userMapper;
    private NotificationUserMapper notificationUserMapper;


    @Override
    public LobbyResDTO createLobby(LobbyReqDTO lobbyReqDTO) {
        Lobby lobby = lobbyMapper.toLobby(lobbyReqDTO);
        lobby.setCodeInvite(generateRandomHexString());
        return lobbyMapper.toLobbyResDTO(lobbyRepo.save(lobby));
    }

    @Override
    public PageResDTO<?> getAllLobby(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<Lobby> lobbies = lobbyRepo.findAll(pageRequest);

        return convertToPageResDTO.convertPageResponse(lobbies,pageRequest,lobbyMapper::toLobbyResDTO);
    }

    @Override
    public LobbyResDTO getLobby(Long lobbyId) {

        return lobbyMapper.toLobbyResDTO(getLobbyById(lobbyId));
    }

    @Override
    public LobbyResDTO updateLobby(Long lobbyId, LobbyReqDTO lobbyReqDTO) {
        Lobby lobby = getLobbyById(lobbyId);
        lobbyMapper.updateLobby(lobby,lobbyReqDTO);

        return lobbyMapper.toLobbyResDTO(lobby);
    }

    @Override
    public void deleteLobby(Long lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        lobby.setActive(false);
        lobbyRepo.save(lobby);
    }

    @Override
    public PageResDTO<?> getAllMembers(Long lobbyId, int page, int size) {
        Lobby lobby = getLobbyById(lobbyId);
        Set<User> users = lobby.getMembers();
        Set<UserLobbyResDTO> userLobbyResDTOS = userMapper.toListUserLobbyResDTO(users);
        LobbyMemberResDTO lobbyMemberResDTO = new LobbyMemberResDTO(lobby.getId(), userLobbyResDTOS);
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items(List.of(lobbyMemberResDTO))
                .build();
    }

    @Override
    public PageResDTO<?> getAllNotifications(Long lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        List<Notification> notifications = lobby.getNotifications();
//        List<LobbyNotificationResDTO> notificationResDTOS = notificationUserMapper.toDto();

        return null;
    }

    private Lobby getLobbyById(Long id){
        return lobbyRepo.findByLobbyId(id).orElseThrow(() -> new NotFoundException("Lobby not found"));
    }
    private static String generateRandomHexString() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[6];
        secureRandom.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }
}
