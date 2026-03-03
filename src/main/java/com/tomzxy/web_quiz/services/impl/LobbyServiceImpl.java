package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserMemberResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.LobbyMapper;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.LobbyRepo;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.RoleRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LobbyServiceImpl implements LobbyService {

    private final LobbyRepo lobbyRepo;
    private final LobbyMapper lobbyMapper;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final QuizRepo quizRepo;

    @Override
    @Transactional
    public LobbyResDTO createLobby(Long userId, LobbyReqDTO lobbyReqDTO) {
        Lobby lobby = lobbyMapper.toLobby(lobbyReqDTO);
        lobby.setCodeInvite(generateRandomHexString());
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        lobby.setHostName(user.getUserName());
        Role hostRole = roleRepo.findByName("HOST")
                .orElseThrow(() -> new NotFoundException("Role not found: HOST"));
        user.setRoles(Set.of(hostRole));
        userRepo.save(user);
        return lobbyMapper.toLobbyResDTO(lobbyRepo.save(lobby));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllLobby(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Lobby> lobbies = lobbyRepo.findAll(pageRequest);
        return convertToPageResDTO.convertPageResponse(lobbies, pageRequest, lobbyMapper::toLobbyResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public LobbyResDTO getLobby(Long lobbyId) {
        return lobbyMapper.toLobbyResDTO(getLobbyById(lobbyId));
    }

    @Override
    public LobbyResDTO updateLobby(Long lobbyId, LobbyReqDTO lobbyReqDTO) {
        Lobby lobby = getLobbyById(lobbyId);
        lobbyMapper.updateLobby(lobby, lobbyReqDTO);
        return lobbyMapper.toLobbyResDTO(lobbyRepo.save(lobby));
    }

    @Override
    public void deleteLobby(Long lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        lobby.setActive(false);
        lobbyRepo.save(lobby);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllMembers(Long lobbyId, int page, int size) {
        Lobby lobby = getLobbyById(lobbyId);
        Set<User> users = lobby.getMembers();
        Set<UserMemberResDTO> userMemberResDTOS = userMapper.toListUserLobbyResDTO(users);
        com.tomzxy.web_quiz.dto.responses.lobby.LobbyMemberResDTO lobbyMemberResDTO = new com.tomzxy.web_quiz.dto.responses.lobby.LobbyMemberResDTO(
                lobby.getId(), userMemberResDTOS);
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items(List.of(lobbyMemberResDTO))
                .build();
    }

    @Override
    public LobbyResDTO addMember(Long lobbyId, Long userId) {
        Lobby lobby = getLobbyById(lobbyId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        lobby.getMembers().add(user);
        return lobbyMapper.toLobbyResDTO(lobbyRepo.save(lobby));
    }

    @Override
    public void removeMember(Long lobbyId, Long userId) {
        Lobby lobby = getLobbyById(lobbyId);
        lobby.getMembers().removeIf(u -> u.getId().equals(userId));
        lobbyRepo.save(lobby);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllNotifications(Long lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        @SuppressWarnings("unchecked")
        List<Object> items = (List<Object>) (List<?>) lobby.getNotifications();
        return PageResDTO.builder()
                .page(0)
                .size(lobby.getNotifications().size())
                .items(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getLobbyByUser(Long userId, int page, int size) {
        List<Lobby> userLobbies = lobbyRepo.findByUserId(userId);
        @SuppressWarnings("unchecked")
        List<Object> dtos = (List<Object>) (List<?>) userLobbies.stream().map(lobbyMapper::toLobbyResDTO).toList();
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items(dtos)
                .total((long) userLobbies.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getGroupQuizzes(Long lobbyId, int page, int size) {
        Lobby lobby = getLobbyById(lobbyId);
        @SuppressWarnings("unchecked")
        List<Object> quizzes = (List<Object>) (List<?>) lobby.getQuizzes();
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items(quizzes)
                .build();
    }

    @Override
    public LobbyResDTO addQuizToGroup(Long lobbyId, Long quizId) {
        Lobby lobby = getLobbyById(lobbyId);
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));
        quiz.setLobby(lobby);
        quizRepo.save(quiz);
        return lobbyMapper.toLobbyResDTO(lobby);
    }

    @Override
    public void removeQuizFromGroup(Long lobbyId, Long quizId) {
        Lobby lobby = getLobbyById(lobbyId);
        lobby.getQuizzes().removeIf(q -> q.getId().equals(quizId));
        lobbyRepo.save(lobby);
    }

    private Lobby getLobbyById(Long id) {
        return lobbyRepo.findByLobbyId(id).orElseThrow(() -> new NotFoundException("Lobby not found: " + id));
    }

    private static String generateRandomHexString() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[6];
        secureRandom.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }
}
