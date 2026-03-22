package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { QuizMapper.class, QuestionMapper.class, AnswerMapper.class })
public interface QuizInstanceMapper {
    QuizInstanceMapper INSTANCE = Mappers.getMapper(QuizInstanceMapper.class);

    // QuizInstance to QuizInstanceResDTO
    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "quizTitle", source = "quiz.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    @Mapping(target = "remainingTimeSeconds", ignore = true) // Will be calculated in service
    @Mapping(target = "timeLimitMinutes", ignore = true)
    @Mapping(target = "shuffleEnabled", ignore = true)
    QuizInstanceResDTO toQuizInstanceResDTO(QuizInstance quizInstance);

    // Helper method to convert status enum to string
    @Named("statusToString")
    default String statusToString(com.tomzxy.web_quiz.enums.QuizInstanceStatus status) {
        return status != null ? status.name() : null;
    }

    // Helper method to convert question type enum to string
    @Named("questionTypeToString")
    default String questionTypeToString(com.tomzxy.web_quiz.enums.ContentType questionType) {
        return questionType != null ? questionType.name() : null;
    }
}