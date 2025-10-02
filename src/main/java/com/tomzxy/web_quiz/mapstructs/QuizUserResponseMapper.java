package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.QuizUserResponseReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizUserResponseResDTO;
import com.tomzxy.web_quiz.models.Quiz.QuizUserResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {QuestionSnapshotMapper.class})
public interface QuizUserResponseMapper {

    @Mapping(target = "quizInstanceId", source = "quizInstance.id")
    @Mapping(target = "questionSnapshots", source = "questionSnapshots")
    @Mapping(target = "isAnswered", expression = "java(quizUserResponse.isAnswered())")
    @Mapping(target = "isNotAnswered", expression = "java(quizUserResponse.isNotAnswered())")
    @Mapping(target = "isAnsweredCorrectly", expression = "java(quizUserResponse.isAnsweredCorrectly())")
    @Mapping(target = "isAnsweredIncorrectly", expression = "java(quizUserResponse.isAnsweredIncorrectly())")
    @Mapping(target = "status", expression = "java(quizUserResponse.getStatus().name())")
    QuizUserResponseResDTO toQuizUserResponseResDTO(QuizUserResponse quizUserResponse);

    @Mapping(target = "quizInstance", ignore = true)
    @Mapping(target = "questionSnapshots", ignore = true)
    QuizUserResponse toQuizUserResponse(QuizUserResponseReqDTO quizUserResponseReqDTO);

    @Mapping(target = "quizInstance", ignore = true)
    @Mapping(target = "questionSnapshots", ignore = true)
    void updateQuizUserResponseFromDto(QuizUserResponseReqDTO quizUserResponseReqDTO, @MappingTarget QuizUserResponse quizUserResponse);

    List<QuizUserResponseResDTO> toQuizUserResponseResDTOList(List<QuizUserResponse> quizUserResponses);
}
