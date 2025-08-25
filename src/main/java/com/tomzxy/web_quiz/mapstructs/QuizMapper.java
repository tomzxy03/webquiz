package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultResDTO;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.QuizResult;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    // Map from DTO to Entity - ignore audit fields and relationships
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "lobby", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "results", ignore = true)
    @Mapping(target = "subject", ignore = true)
    Quiz toEntity(QuizReqDTO dto);

    // Map from Entity to DTO - handle nested mappings
    @Mapping(target = "hostName", source = "host.userName")
    @Mapping(target = "lobbyName", source = "lobby.lobbyName")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    @Mapping(target = "submissions", source = "results", qualifiedByName = "mapResultsToSubmissions")
    QuizResDTO toDto(Quiz entity);


    @Mapping(target = "hostName", source = "host.userName")
    @Mapping(target = "lobbyName", source = "lobby.lobbyName")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    @Mapping(target = "submissions", source = "results", qualifiedByName = "mapResultsToSubmissions")
    List<QuizResDTO> toListDto(List<Quiz> quizs);

    @Named("mapResultsToSubmissions")
    default Set<QuizResultResDTO> mapResultsToSubmissions(Set<QuizResult> results) {
        if (results == null) {
            return null;
        }
        return results.stream()
                .map(this::mapQuizResultToResDTO)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Named("mapQuizResultToResDTO")
    default QuizResultResDTO mapQuizResultToResDTO(QuizResult result) {
        if (result == null) {
            return null;
        }
        
        return QuizResultResDTO.builder()
                .id(result.getId())
                .quizTitle(result.getQuiz() != null ? result.getQuiz().getTitle() : null)
                .userId(result.getUser() != null ? result.getUser().getId() : null)
                .userName(result.getUser() != null ? result.getUser().getUserName() : null)
                .score(result.getScore() != null ? result.getScore().doubleValue() : null)
                .totalQuestions(result.getTotalQuestions())
                .correctAnswers(result.getTotalCorrected())
                .wrongAnswers(result.getTotalFailed())
                .skippedQuestions(result.getTotalSkipped())
                .timeSpentSeconds(result.getCompletionTimeMinutes() != null ? result.getCompletionTimeMinutes() * 60 : null)
                .completedAt(result.getCompletedAt())
                .isPassed(result.isPassed())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .isActive(result.isActive())
                .build();
    }
}