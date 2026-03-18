package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyNotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyQuizResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserMemberResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.LobbyRole;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.ExistedException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.exception.UnAuthorizedException;
import com.tomzxy.web_quiz.mapstructs.LobbyMapper;
import com.tomzxy.web_quiz.mapstructs.Notification.NotificationMapper;
import com.tomzxy.web_quiz.mapstructs.QuizMapper;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.models.Host.LobbyMemberId;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.NotificationUser.Notification;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Subject;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.LobbyService;
import com.tomzxy.web_quiz.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LobbyServiceImpl implements LobbyService {

    private final LobbyRepo lobbyRepo;
    private final LobbyMapper lobbyMapper;
    private final NotificationMapper notificationMapper;
    private final QuizMapper quizMapper;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final QuizRepo quizRepo;
    private final SubjectRepo subjectRepo;
    private final LobbyMemberRepo lobbyMemberRepo;
    private final NotificationRepo notificationRepo;


    @Override
    @Transactional
    public LobbyResDTO createLobby(LobbyReqDTO lobbyReqDTO) {

        log.info("Create lobby: {}", lobbyReqDTO.getLobbyName());

        Lobby lobby = lobbyMapper.toLobby(lobbyReqDTO);
        lobby.setCodeInvite(generateInviteCode());
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnAuthorizedException("User not authenticated");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        lobby.setHost(user);
        Lobby savedLobby = lobbyRepo.save(lobby);
        // tạo host member
        LobbyMemberId id = new LobbyMemberId(savedLobby.getId(), userId);
        LobbyMember lobbyMember = LobbyMember.builder()
                .id(id)
                .lobby(savedLobby)
                .user(user)
                .role(LobbyRole.HOST)
                .build();

        lobbyMemberRepo.save(lobbyMember);

        log.info("Lobby created successfully");

        LobbyResDTO lobbyResDTO = lobbyMapper.toLobbyResDTO(savedLobby);

        return lobbyResDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllLobby(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Lobby> lobbies = lobbyRepo.findAll(pageRequest);
        return convertToPageResDTO.convertPageResponse(lobbies, pageRequest, lobbyMapper::toLobbyResDTO);
    }
    @Transactional(readOnly = true)
    @Override
    public PageResDTO<LobbyResDTO> getAllLobbyOwned(int page, int size) {

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnAuthorizedException("User not authenticated");
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<LobbyResDTO> lobbies =
                lobbyMemberRepo.findOwnedLobbies(userId, LobbyRole.HOST, pageRequest);

        return PageResDTO.<LobbyResDTO>builder()
                .page(page)
                .size(size)
                .total(lobbies.getTotalElements())
                .items(lobbies.getContent())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllLobbyJoined(int page, int size){
        Long userId = SecurityUtils.getCurrentUserId();
        if(userId == null){
            throw new UnAuthorizedException("User not authenticated");
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LobbyResDTO> lobbies = lobbyRepo.findJoinedLobbies(userId,pageRequest);
        return PageResDTO.<LobbyResDTO>builder()
                .page(page)
                .size(size)
                .total(lobbies.getTotalElements())
                .items(lobbies.getContent())
                .build();
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

    @Transactional(readOnly = true)
    @Override
    public PageResDTO<UserMemberResDTO> getAllMembers(Long lobbyId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<LobbyMember> members =
                lobbyMemberRepo.findByIdLobbyId(lobbyId, pageable);
        log.info("Get members for lobby: {}", members.getContent());

        List<UserMemberResDTO> items = members.getContent()
                .stream()
                .map(userMapper::toUserMemberResDTO)
                .collect(Collectors.toList());

        return PageResDTO.<UserMemberResDTO>builder()
                .page(page)
                .size(size)
                .total(members.getTotalElements())
                .items(items)
                .build();
    }

    @Override
    public LobbyResDTO addMember(Long lobbyId, Long userId) {

        Lobby lobby = getLobbyById(lobbyId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        boolean exists = lobbyMemberRepo.existsByIdLobbyIdAndIdUserId(lobbyId, userId);

        if (exists) {
            throw new ExistedException(AppCode.DATA_EXISTED,"User already in lobby");
        }

        LobbyMember member = new LobbyMember();
        member.setLobby(lobby);
        member.setUser(user);
        member.setJoinedAt(LocalDateTime.now());
        member.setRole(LobbyRole.MEMBER);

        lobbyMemberRepo.save(member);

        return lobbyMapper.toLobbyResDTO(lobby);
    }

    @Override
    public void removeMember(Long lobbyId, Long userId) {
        Lobby lobby = getLobbyById(lobbyId);
        checkOwned(lobby);
        lobbyMemberRepo.deleteByIdLobbyIdAndIdUserId(lobbyId, userId);
        lobbyRepo.save(lobby);
    }
    @Override
    public void leaveLobby(Long lobbyId) {

        Long userId = SecurityUtils.getCurrentUserId();

        LobbyMember member = lobbyMemberRepo
                .findByIdLobbyIdAndIdUserId(lobbyId, userId)
                .orElseThrow( () -> new NotFoundException("Member not found"));

        if (member.getRole() == LobbyRole.HOST) {
            throw new ApiException(AppCode.BAD_REQUEST,"Owner cannot leave lobby");
        }

        lobbyMemberRepo.delete(member);
    }
    //===========Notification==============//
    @Transactional(readOnly = true)
    @Override
    public PageResDTO<?> getAllNotifications(Long lobbyId, int page, int size) {
        PageRequest pageable = PageRequest.of(page,size);
        Lobby lobby = getLobbyById(lobbyId);
        Page<Notification> pageData =
                notificationRepo.findByLobbyIdAndIsActiveTrue(lobbyId, pageable);
        return PageResDTO.<Notification>builder()
                .page(page)
                .size(size)
                .total(pageData.getTotalElements())
                .items(pageData.getContent())
                .build();
    }
    @Override
    @Transactional
    public LobbyNotificationResDTO addNotification(Long lobbyId, NotificationReqDTO notificationReqDTO) {
        Lobby lobby = getLobbyById(lobbyId);

        User host = checkOwned(lobby);
        Notification notification = notificationMapper.toEntity(notificationReqDTO);
        notification.setHost(host);
        notification.setLobby(lobby);
        notificationRepo.save(notification);
        return LobbyNotificationResDTO.builder()
                .id(lobby.getId())
                .notificationResDTOS(List.of(notificationMapper.toDto(notification)))
                .build();
    }
    @Transactional
    @Override
    public LobbyNotificationResDTO updateNotification(Long lobbyId, Long notificationId, NotificationReqDTO notificationReqDTO){
        Lobby lobby = getLobbyById(lobbyId);
        User host = checkOwned(lobby);
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notificationMapper.updateNotification(notification, notificationReqDTO);
        notification.setLobby(lobby);
        notification.setHost(host);
        notificationRepo.save(notification);
        return LobbyNotificationResDTO.builder()
                .id(lobby.getId())
                .notificationResDTOS(List.of(notificationMapper.toDto(notification)))
                .build();
    }
    @Override
    public void deleteNotification(Long lobbyId, Long notificationId) {
        Lobby lobby = getLobbyById(lobbyId);
        User host = checkOwned(lobby);
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notification.setActive(false);
        notificationRepo.save(notification);
    }
    @Override
    public LobbyResDTO joinLobby(String code) {

        Lobby lobby = lobbyRepo.findByJoinCode(code)
                .orElseThrow(() -> new NotFoundException("Lobby not found"));

        Long userId = SecurityUtils.getCurrentUserId();

        boolean exists = lobbyMemberRepo.existsByIdLobbyIdAndIdUserId(lobby.getId(), userId);

        if (exists) {
            throw new ExistedException(AppCode.DATA_EXISTED,"Already joined");
        }
        if (userId == null) {
            throw new UnAuthorizedException("User not authenticated");
        }
        User user = userRepo.findById(userId).orElseThrow();

        LobbyMember member = new LobbyMember();
        member.setLobby(lobby);
        member.setUser(user);
        member.setRole(LobbyRole.MEMBER);
        member.setJoinedAt(LocalDateTime.now());

        lobbyMemberRepo.save(member);

        return lobbyMapper.toLobbyResDTO(lobby);
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
    public PageResDTO<QuizResDTO> getGroupQuizzes(Long lobbyId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
    
        // 1. Truy vấn thẳng từ Repo để Database làm việc phân trang hộ mình
        Page<Quiz> quizPage = quizRepo.findByLobbyId(lobbyId, pageable);
    
        // 2. Map trực tiếp từ Quiz Entity sang QuizResDTO
        List<QuizResDTO> items = quizPage.getContent().stream()
            .map(quiz -> {
                QuizResDTO dto = quizMapper.toDto(quiz); // Dùng mapper bạn đã có
                // Bổ sung thêm các trường mà Mapper chưa có (nếu cần)
                dto.setLobbyName(quiz.getLobby().getLobbyName()); 
                dto.setHostName(quiz.getHost().getUserName());
                return dto;
            })
            .collect(Collectors.toList());

        // 3. Trả về PageResDTO chứa danh sách Quiz "sạch"
        return PageResDTO.<QuizResDTO>builder()
            .page(page)
            .size(size)
            .total(quizPage.getTotalElements())
            .total_page(quizPage.getTotalPages()) // Nên thêm cả tổng số trang
            .items(items)
            .build();
}

    @Override
    @Transactional
    public LobbyQuizResDTO addQuizToGroup(Long lobbyId, QuizReqDTO dto) {

        Lobby lobby = getLobbyById(lobbyId);
        User user = checkOwned(lobby);

        Subject subject = subjectRepo.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        Quiz quiz = quizMapper.toEntity(dto);

        quiz.setHost(user);
        quiz.setSubject(subject);
        quiz.setLobby(lobby);

        quiz = quizRepo.save(quiz);

        LobbyQuizResDTO res = new LobbyQuizResDTO();
        res.setId(lobby.getId());
        res.setQuizzes(Set.of(quizMapper.toDto(quiz)));

        return res;
    }
    @Override
    @Transactional
    public LobbyQuizResDTO updateQuizInGroup(Long lobbyId, Long quizId, QuizReqDTO dto) {

        Lobby lobby = getLobbyById(lobbyId);
        checkOwned(lobby);

        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        Subject subject = subjectRepo.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTimeLimitMinutes(dto.getTimeLimitMinutes());
        quiz.setVisibility(dto.getVisibility());
        quiz.setMaxAttempt(dto.getMaxAttempt() != null ? dto.getMaxAttempt() : 1);
        quiz.setSubject(subject);
        quiz.setLobby(lobby);

        if (dto.getStartAt() != null) {
            quiz.setStartDate(dto.getStartAt());
        }

        if (dto.getEndAt() != null) {
            quiz.setEndDate(dto.getEndAt());
        }

        quizRepo.save(quiz);

        LobbyQuizResDTO res = new LobbyQuizResDTO();
        res.setId(lobby.getId());
        res.setQuizzes(Set.of(quizMapper.toDto(quiz)));

        return res;
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
    private String generateInviteCode(){
        String code;
        do{
            code = generateRandomHexString();
        }while(lobbyRepo.existsByCodeInvite(code));

        return code;
    }
    private static String generateRandomHexString() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[6];
        secureRandom.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }

    private User checkOwned(Lobby lobby){
        String email = SecurityUtils.getCurrentUsername();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));
        LobbyMember member = lobbyMemberRepo
                .findByIdLobbyIdAndIdUserId(lobby.getId(), user.getId())
                .orElseThrow(() -> new ApiException(AppCode.FORBIDDEN,"Not in lobby"));

        if(member.getRole() != LobbyRole.HOST){
            throw new ApiException(AppCode.FORBIDDEN,"Not lobby host");
        }
        log.info("===================================user: {}", user);
        return user;
    }
}
