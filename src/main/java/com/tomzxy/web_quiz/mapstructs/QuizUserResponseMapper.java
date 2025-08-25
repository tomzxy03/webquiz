package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.QuizUserResponseReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizUserResponseResDTO;
import com.tomzxy.web_quiz.models.QuizUserResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizUserResponseMapper {

    QuizUserResponseResDTO toQuizUserResponseResDTO(QuizUserResponse quizUserResponse);

    QuizUserResponse toQuizUserResponse(QuizUserResponseReqDTO quizUserResponseReqDTO);

    void updateQuizUserResponseFromDto(QuizUserResponseReqDTO quizUserResponseReqDTO, @MappingTarget QuizUserResponse quizUserResponse);

    List<QuizUserResponseResDTO> toQuizUserResponseResDTOList(List<QuizUserResponse> quizUserResponses);
} 