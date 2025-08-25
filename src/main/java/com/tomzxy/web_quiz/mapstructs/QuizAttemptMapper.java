package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.QuizAttemptReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizAttemptResDTO;
import com.tomzxy.web_quiz.models.Quiz.QuizAttempt;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizAttemptMapper {

    @Mapping(target = "answersSnapshot", expression = "java(mapAnswersSnapshotToString(quizAttempt.getAnswersSnapshot()))")
    QuizAttemptResDTO toQuizAttemptResDTO(QuizAttempt quizAttempt);

    @Mapping(target = "answersSnapshot", ignore = true)
    QuizAttempt toQuizAttempt(QuizAttemptReqDTO quizAttemptReqDTO);

    @Mapping(target = "answersSnapshot", ignore = true)
    void updateQuizAttemptFromDto(QuizAttemptReqDTO quizAttemptReqDTO, @MappingTarget QuizAttempt quizAttempt);

    List<QuizAttemptResDTO> toQuizAttemptResDTOList(List<QuizAttempt> quizAttempts);

    // Custom mapping methods
    default String mapAnswersSnapshotToString(com.tomzxy.web_quiz.models.AnswersSnapshot answersSnapshot) {
        if (answersSnapshot == null) return null;
        return answersSnapshot.getAllAnswerOptions();
    }
} 