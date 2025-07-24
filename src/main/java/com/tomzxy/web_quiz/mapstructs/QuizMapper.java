package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.models.Quiz;
import com.tomzxy.web_quiz.dto.requests.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {QuizQuestionMapper.class})
public interface QuizMapper {


    Quiz toEntity(QuizReqDTO dto);


    QuizResDTO toDto(Quiz entity);
}