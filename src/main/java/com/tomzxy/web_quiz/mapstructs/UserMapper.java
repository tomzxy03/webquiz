package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.user.UserReqDto;
import com.tomzxy.web_quiz.dto.responses.user.UserMemberResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.dto.requests.user.UserProfileReqDTO;
import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.User.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUser(UserReqDto userReqDto);

    @Mapping(target = "roles", expression = "java(user.getRoles() != null ? user.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toSet()) : null)")
    UserResDTO toUserResDTO(User user);

    List<UserResDTO> toListUserResDTO(List<User> users);

    @Mapping(target = "roleName", source = "roles", qualifiedByName = "mapRoleName")
    UserMemberResDTO toUserMemberResDTO(User user);

    Set<UserMemberResDTO> toListUserLobbyResDTO(Set<User> users);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", source = "password")
    void updateUser(@MappingTarget User user, UserReqDto userReqDto);

    void updateUserProfile(@MappingTarget User user, UserProfileReqDTO userProfileReqDTO);

    @Named("mapRoleName")
    default String mapRoleName(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.iterator().next().getName();
    }
}
