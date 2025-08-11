package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;
import com.tomzxy.web_quiz.models.Question;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = AnswerMapper.class)
public interface QuestionMapper {
    QuestionMapper MAPPER = Mappers.getMapper(QuestionMapper.class);


    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "subject", ignore = true)
    Question toQuestion(QuestionReqDTO questionReqDto);

    @Mapping(source = "answers", target = "answers")
    @Mapping(source =  "subject.subjectName", target="subjectName")
    @Mapping(source = "active", target = "isActive")
    QuestionResDTO toQuestionResDTO(Question question);

    @Mapping(source = "answers", target = "answers")
    @Mapping(source =  "subject.subjectName",target= "subjectName" )
    @Mapping(source = "active", target = "isActive")
    List<QuestionResDTO> toListQuestionResDTO(List<Question> questions);

    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "subject", ignore = true)

    List<Question> toListQuestion(List<QuestionReqDTO> questionReqDTOS);

    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "subject", ignore = true)
    void updateQuestion(@MappingTarget Question question, QuestionReqDTO questionReqDTO);

//    @AfterMapping
//    default void linkAnswers(@MappingTarget Question question) {
//        if (question.getAnswers() != null) {
//            question.getAnswers().forEach(a -> a.setQuestion(question));
//        }
//    }

}
