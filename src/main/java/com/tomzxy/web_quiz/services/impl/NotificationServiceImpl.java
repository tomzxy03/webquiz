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
        if(notificationReqDTO.getLobbyId()!=null){
            User user = userRepo.findByIdAndActive(notificationReqDTO.getHostId()).
                    orElseThrow(()-> new NotFoundException("User not found!"));
            notification.setHost(user);
        }
        if(notificationReqDTO.getLobbyId()!=null){
            Lobby lobby = lobbyRepo.findByLobbyId(notificationReqDTO.getLobbyId()).
                    orElseThrow(()-> new NotFoundException("Lobby not found!"));
            notification.setLobby(lobby);
        }
        try{
            notificationRepo.save(notification);
            processAutoLinkUser(notification);
        }catch (Exception e){
            throw new ApiException(AppCode.UNKNOWN_ERROR, "Error save notification: "+ e.getMessage());
        }
        return null;
    }
    private void processAutoLinkUser(Notification notification){
        if(notification.getType().equals(NotificationType.SYSTEM)){

        }else{

        }
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
