package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.AnswerResDTO;
import com.tomzxy.web_quiz.models.Answer;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = Answer.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {
    AnswerMapper MAPPER = Mappers.getMapper(AnswerMapper.class);

    Answer toAnswer(AnswerReqDTO answerReqDTO);

    AnswerResDTO toAnswerResDTO(Answer answer);

    List<AnswerResDTO> toListAnswerResDTO(List<Answer> answers);

    void updateAnswer(@MappingTarget Answer answer, AnswerReqDTO answerReqDTO);



}
