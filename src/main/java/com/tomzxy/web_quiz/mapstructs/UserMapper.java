package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;
import com.tomzxy.web_quiz.models.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = User.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", ignore = true)
    User toUser(UserReqDto userReqDto);

    @Mapping(target = "roles", ignore = true)
    UserResDTO toUserResDTO(User user);

    List<UserResDTO> toListUserResDTO(List<User> users);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserReqDto userReqDto);



}
