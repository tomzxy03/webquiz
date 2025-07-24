package com.tomzxy.web_quiz.mapstructs;


import com.tomzxy.web_quiz.dto.requests.PermissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.PermissionResDTO;

import com.tomzxy.web_quiz.models.Permission;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionMapper MAPPER = Mappers.getMapper(PermissionMapper.class);

    Permission toPermission(PermissionReqDTO permissionReqDTO);
    
    PermissionResDTO toPermissionResDTO(Permission permission);

    List<PermissionResDTO> toListPermissionResDTO(List<Permission> permissions);

    void updatePermission(@MappingTarget Permission permission, PermissionReqDTO permissionReqDTO);

}
