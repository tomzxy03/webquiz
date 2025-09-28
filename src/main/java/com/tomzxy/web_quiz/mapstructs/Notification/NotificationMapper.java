package com.tomzxy.web_quiz.mapstructs.Notification;


import com.tomzxy.web_quiz.dto.responses.NotificationResDTO;
import com.tomzxy.web_quiz.models.Notification;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationMapper MAPPER = Mappers.getMapper(NotificationMapper.class);

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "host.id", target = "hostId")
    NotificationResDTO toDto(Notification entity);

    @InheritInverseConfiguration
    @Mapping(target = "group", ignore = true) // xử lý qua service
    @Mapping(target = "host", ignore = true)  // xử lý qua service
    Notification toEntity(NotificationResDTO dto);

}
