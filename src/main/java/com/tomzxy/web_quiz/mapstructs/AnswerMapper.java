package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.AnswerResDTO;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.Question;
import org.aspectj.lang.annotation.After;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = Answer.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {
    AnswerMapper MAPPER = Mappers.getMapper(AnswerMapper.class);

    @Mapping(target ="question", ignore = true )
    Answer toAnswer(AnswerReqDTO answerReqDTO);

    AnswerResDTO toAnswerResDTO(Answer answer);

    List<AnswerResDTO> toListAnswerResDTO(List<Answer> answers);

    @Mapping(target = "question", ignore = true)
    Set<Answer> toListAnswer(Set<AnswerReqDTO> answers);
    
    void updateAnswer(@MappingTarget Answer answer, AnswerReqDTO answerReqDTO);

}
