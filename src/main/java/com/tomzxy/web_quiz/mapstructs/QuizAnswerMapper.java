package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.models.QuizAnswer;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.dto.requests.QuizAnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizAnswerResDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizAnswerMapper {

    @Mapping(target = "quizQuestion", ignore = true) // Liên kết trong service
    @Mapping(target = "answer", source = "answerId", qualifiedByName = "mapAnswerBank")
    QuizAnswer toEntity(QuizAnswerReqDTO dto);

    @Mapping(target = "answerId", source = "answer.id")
    QuizAnswerResDTO toDTO(QuizAnswer entity);

    List<QuizAnswer> toEntityList(List<QuizAnswerReqDTO> dtoList);

    List<QuizAnswerResDTO> toDTOList(List<QuizAnswer> entityList);

    @Named("mapAnswerBank")
    default Answer mapAnswerBank(Long id) {
        if (id == null) return null;
        Answer answer = new Answer();
        answer.setId(id);
        return answer;
    }
}

