package com.tomzxy.web_quiz.mapstructs.Notification;


import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.responses.Notification.NotificationResDTO;
import com.tomzxy.web_quiz.models.NotificationUser.Notification;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationMapper MAPPER = Mappers.getMapper(NotificationMapper.class);

    @Mapping(target = "lobbyName", ignore = true)
    @Mapping(target = "hostName", ignore = true)
    @Mapping(target = "type", ignore = true)
    NotificationResDTO toDto(Notification entity);

    @InheritInverseConfiguration
    @Mapping(target = "lobby", ignore = true) // xử lý qua service
    @Mapping(target = "host", ignore = true)  // xử lý qua service
    Notification toEntity(NotificationReqDTO dto);

    @Mapping(target = "lobby", ignore = true)
    @Mapping(target = "host", ignore = true)
    void updateNotification(@MappingTarget Notification notification, NotificationReqDTO notificationReqDTO);

}
