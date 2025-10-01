package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.responses.NotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.NotificationType;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.GlobalExceptionHandler;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.Notification.NotificationMapper;
import com.tomzxy.web_quiz.mapstructs.Notification.NotificationUserMapper;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.Notification;
import com.tomzxy.web_quiz.models.NotificationUser.NotificationUserId;
import com.tomzxy.web_quiz.models.NotificationUser.UserNotification;
import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.repositories.LobbyRepo;
import com.tomzxy.web_quiz.repositories.NotificationRepo;
import com.tomzxy.web_quiz.repositories.NotificationUserRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private NotificationRepo notificationRepo;
    private NotificationMapper notificationMapper;
    private ConvertToPageResDTO convertToPageResDTO;
    private NotificationUserMapper notificationUserMapper;
    private NotificationUserRepo notificationUserRepo;
    private UserRepo userRepo;
    private LobbyRepo lobbyRepo;

    @Override
    public PageResDTO<?> getAllNotification(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<Notification> notifications = notificationRepo.findAll(pageRequest);

        return convertToPageRes(notifications);

    }

    @Override
    public NotificationResDTO getNotificationId(Long notificationId) {
        Notification notification = getById(notificationId);
        NotificationResDTO notificationResDTO = notificationMapper.toDto(notification);
        notificationResDTO.setLobbyName(notification.getLobby().getLobbyName());
        notificationResDTO.setHostName(notification.getHost().getUserName());
        notificationResDTO.setType(notification.getType().name());
        return notificationResDTO;
    }

    @Override
    @Transactional
    public NotificationResDTO createNotification(NotificationReqDTO notificationReqDTO) {
        Notification notification = notificationMapper.toEntity(notificationReqDTO);
        Lobby lobby= new Lobby();
        User user = new User();
        if(notificationReqDTO.getLobbyId()!=null){
            user = userRepo.findByIdAndActive(notificationReqDTO.getHostId()).
                    orElseThrow(()-> new NotFoundException("User not found!"));
            notification.setHost(user);
        }
        if(notificationReqDTO.getLobbyId()!=null){
            lobby = lobbyRepo.findByLobbyId(notificationReqDTO.getLobbyId()).
                    orElseThrow(()-> new NotFoundException("Lobby not found!"));
            notification.setLobby(lobby);
        }
        try{
            notificationRepo.save(notification);
            switch (notification.getType()){
                case GROUP -> linkToLobbyUsers(lobby, notification);
                case SYSTEM -> linkToAllUsers(notification);
            }
        }catch (Exception e){
            throw new ApiException(AppCode.UNKNOWN_ERROR, "Error save notification: "+ e.getMessage());
        }
        return notificationMapper.toDto(notification);
    }
    private void linkToAllUsers(Notification notification) {
        List<User> users = userRepo.findAll();
        List<UserNotification> links = users.stream()
                .map(user -> new UserNotification(new NotificationUserId(user.getId(), notification.getId()), user, notification, false, null))
                .toList();
        notificationUserRepo.saveAll(links);
    }

    private void linkToLobbyUsers(Lobby lobby, Notification notification) {
        List<User> users = lobby.getMembers().stream().toList();
        List<UserNotification> links = users.stream()
                .map(user -> new UserNotification(new NotificationUserId(user.getId(), notification.getId()), user, notification, false, null))
                .toList();
        notificationUserRepo.saveAll(links);
    }

    private void linkToSingleUser(Long userId, Notification notification) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        UserNotification link = new UserNotification(new NotificationUserId(user.getId(), notification.getId()), user, notification, false, null);
        notificationUserRepo.save(link);
    }

    @Override
    public NotificationResDTO updateNotification(Long notificationId, NotificationReqDTO notificationReqDTO) {
        return null;
    }

    @Override
    public Void deleteNotification(Long notificationId) {
        return null;
    }

    private Notification getById(Long notificationId){
        return notificationRepo.findById(notificationId).orElseThrow(()-> new NotFoundException("Notification data not found"));
    }

    @Override
    public PageResDTO<NotificationResDTO> getUserNotifications(Long userId, int page, int size){
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<UserNotification> userNotifications = notificationUserRepo.findByIdUserId(userId, pageRequest);
        List<NotificationResDTO> dtoList = userNotifications.stream().map(
                entity -> {
                    return notificationMapper.toDto(entity.getNotification());
                }

        ).toList();
        return PageResDTO.<NotificationResDTO>builder()
                .page(page)
                .size(size)
                .total_page(userNotifications.getTotalPages())
                .items(dtoList)
                .build();
    }

    @Transactional
    @Override
    public void markAsRead(Long userId, Long notificationId) {
        UserNotification userNotification = notificationUserRepo.findById(new NotificationUserId(userId, notificationId))
                .orElseThrow(() -> new NotFoundException("UserNotification not found"));
        userNotification.setRead(true);
        userNotification.setReadAt(LocalDateTime.now());
        notificationUserRepo.save(userNotification);
    }

    private PageResDTO<NotificationResDTO> convertToPageRes(Page<Notification> notifications){
        List<NotificationResDTO> items = notifications.stream()
                .map(entity ->{
                    NotificationResDTO dto = notificationMapper.toDto(entity);
                    if(entity.getHost()!=null){
                        dto.setHostName(entity.getHost().getUserName());
                    }
                    if(entity.getLobby()!=null){
                        dto.setLobbyName(entity.getLobby().getLobbyName());
                    }
                    return dto;
                }).toList();
        return PageResDTO.<NotificationResDTO>builder()
                .page(notifications.getNumber())
                .size(notifications.getSize())
                .total_page(notifications.getTotalPages())
                .items(items)
                .build();
    }

}
