package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.models.Quiz.Quiz;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    // Map from DTO to Entity - ignore audit fields and relationships
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "lobby", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "instances", ignore = true)
    @Mapping(target = "subject", ignore = true)
    Quiz toEntity(QuizReqDTO dto);

    // Map from Entity to DTO - handle nested mappings
    @Mapping(target = "hostName", source = "host.userName")
    @Mapping(target = "lobbyName", source = "lobby.lobbyName")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    QuizResDTO toDto(Quiz entity);


    @Mapping(target = "hostName", source = "host.userName")
    @Mapping(target = "lobbyName", source = "lobby.lobbyName")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    List<QuizResDTO> toListDto(List<Quiz> quizs);

}