package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.requests.UserProfileReqDTO;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;
import com.tomzxy.web_quiz.models.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.lang.annotation.Target;
import java.util.List;

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

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    void updateUser(@MappingTarget User user, UserReqDto userReqDto);

    void updateUserProfile(@MappingTarget User user, UserProfileReqDTO userProfileReqDTO);



}
