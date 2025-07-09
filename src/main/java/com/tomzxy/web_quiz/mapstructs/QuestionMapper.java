package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;
import com.tomzxy.web_quiz.models.Question;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = Question.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionMapper {
    QuestionMapper MAPPER = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "answers", ignore = true)
    Question toQuestion(QuestionReqDTO questionReqDto);

    @Mapping(target = "answers", ignore = true)
    QuestionResDTO toQuestionResDTO(Question question);

    @Mapping(target = "answers", ignore = true)
    List<QuestionResDTO> toListQuestionResDTO(List<Question> questions);

    @Mapping(target = "answers", ignore = true)
    void updateQuestion(@MappingTarget Question question, QuestionReqDTO questionReqDTO);



}
