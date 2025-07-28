package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.models.Quiz;
import com.tomzxy.web_quiz.dto.requests.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    // Map from DTO to Entity - ignore audit fields and relationships
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "subject", ignore = true)
    Quiz toEntity(QuizReqDTO dto);

    // Map from Entity to DTO - handle nested mappings
    @Mapping(target = "hostName", source = "host.userName")
    @Mapping(target = "groupName", source = "group.groupName")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    @Mapping(target = "submissions", ignore = true) // Will be handled manually in service
    QuizResDTO toDto(Quiz entity);
}