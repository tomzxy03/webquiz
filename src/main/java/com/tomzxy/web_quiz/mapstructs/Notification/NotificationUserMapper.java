package com.tomzxy.web_quiz.mapstructs.Notification;

import com.tomzxy.web_quiz.dto.responses.NotificationUserResDTO;
import com.tomzxy.web_quiz.models.NotificationUser.UserNotification;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface NotificationUserMapper {

    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "id.notificationId", target = "notificationId")
    @Mapping(source = "notification", target = "notification")
    NotificationUserResDTO toDto(UserNotification entity);

    @InheritInverseConfiguration
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "notification", ignore = true)
    UserNotification toEntity(NotificationUserResDTO dto);
}
