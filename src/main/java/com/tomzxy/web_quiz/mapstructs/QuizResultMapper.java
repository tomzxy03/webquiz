package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.QuizResultReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultResDTO;
import com.tomzxy.web_quiz.models.QuizResult;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizResultMapper {

    QuizResultResDTO toQuizResultResDTO(QuizResult quizResult);

    QuizResult toQuizResult(QuizResultReqDTO quizResultReqDTO);

    void updateQuizResultFromDto(QuizResultReqDTO quizResultReqDTO, @MappingTarget QuizResult quizResult);

    List<QuizResultResDTO> toQuizResultResDTOList(List<QuizResult> quizResults);

    @Named("performanceGradeToString")
    default String performanceGradeToString(String performanceGrade) {
        return performanceGrade != null ? performanceGrade : "N/A";
    }

    @Named("performanceLevelToString")
    default String performanceLevelToString(String performanceLevel) {
        return performanceLevel != null ? performanceLevel : "N/A";
    }

    @Named("timeManagementToString")
    default String timeManagementToString(String timeManagement) {
        return timeManagement != null ? timeManagement : "N/A";
    }
} 