package com.tomzxy.web_quiz.mapstructs;

import java.util.List;
import java.util.Set;


import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.answer.AnswerResDTO;
import com.tomzxy.web_quiz.models.Answer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    AnswerMapper MAPPER = Mappers.getMapper(AnswerMapper.class);

    Answer toAnswer(AnswerReqDTO answerReqDTO);

    @Mapping(source = "answerName", target = "answerText")
    AnswerResDTO toAnswerResDTO(Answer answer);
    List<AnswerResDTO> toListAnswerResDTO(List<Answer> answers);
    Set<Answer> toSetAnswer(Set<AnswerReqDTO> answerReqDTOs);
    void updateAnswer(@MappingTarget Answer answer, AnswerReqDTO answerReqDTO);
}
