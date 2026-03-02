package com.tomzxy.web_quiz.mapstructs;


import com.tomzxy.web_quiz.dto.requests.RoleReqDTO;
import com.tomzxy.web_quiz.dto.responses.RoleResDTO;
import com.tomzxy.web_quiz.models.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = Role.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleMapper MAPPER = Mappers.getMapper(RoleMapper.class);

    Role toRole(RoleReqDTO roleReqDTO);
    
    RoleResDTO toRoleResDTO(Role role);

    List<RoleResDTO> toListRoleResDTO(List<Role> roles);

    void updateRole(@MappingTarget Role role, RoleReqDTO roleReqDTO);

}
