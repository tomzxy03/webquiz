package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.QuizQuestion;
import com.tomzxy.web_quiz.dto.requests.QuizQuestionReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizQuestionResDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {QuizAnswerMapper.class})
public interface QuizQuestionMapper {

    @Mapping(target = "quiz", ignore = true) // Quiz sẽ được set sau trong service
    @Mapping(target = "question", source = "questionId", qualifiedByName = "mapQuestionBank")
    QuizQuestion toEntity(QuizQuestionReqDTO dto);

    @Mapping(target = "questionId", source = "question.id")
    QuizQuestionResDTO toDTO(QuizQuestion entity);

    List<QuizQuestion> toEntityList(List<QuizQuestionReqDTO> dtoList);

    List<QuizQuestionResDTO> toDTOList(List<QuizQuestion> entityList);

    @Named("mapQuestionBank")
    default Question mapQuestionBank(Long id) {
        if (id == null) return null;
        Question question = new Question();
        question.setId(id);
        return question;
    }
}
