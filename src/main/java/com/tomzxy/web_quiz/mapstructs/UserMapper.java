package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.responses.user.UserLobbyResDTO;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.dto.requests.UserProfileReqDTO;
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
    @Mapping(target = "passwordHash", source = "password")
    User toUser(UserReqDto userReqDto);

    @Mapping(target = "roles", expression = "java(user.getRoles() != null ? user.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toSet()) : null)")
    @Mapping(target = "dateOfBirth", expression = "java(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)")
    UserResDTO toUserResDTO(User user);

    List<UserResDTO> toListUserResDTO(List<User> users);


    Set<UserLobbyResDTO> toListUserLobbyResDTO(Set<User> users);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    void updateUser(@MappingTarget User user, UserReqDto userReqDto);

    void updateUserProfile(@MappingTarget User user, UserProfileReqDTO userProfileReqDTO);



}
