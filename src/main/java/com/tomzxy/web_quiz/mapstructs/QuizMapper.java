package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultResDTO;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizInstance;

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
    @Mapping(target = "instances", ignore = true)
    @Mapping(target = "subject", ignore = true)
    Quiz toEntity(QuizReqDTO dto);

    // Map from Entity to DTO - handle nested mappings
    @Mapping(target = "hostName", source = "host.userName")
    @Mapping(target = "lobbyName", source = "lobby.lobbyName")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    @Mapping(target = "submissions", source = "instances", qualifiedByName = "mapResultsToSubmissions")
    QuizResDTO toDto(Quiz entity);


    @Mapping(target = "hostName", source = "host.userName")
    @Mapping(target = "lobbyName", source = "lobby.lobbyName")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    @Mapping(target = "submissions", source = "instances", qualifiedByName = "mapResultsToSubmissions")
    List<QuizResDTO> toListDto(List<Quiz> quizs);

    @Named("mapResultsToSubmissions")
    default Set<QuizResultResDTO> mapResultsToSubmissions(Set<QuizInstance> instances) {
        if (instances == null) {
            return null;
        }
        return instances.stream()
                .map(this::mapQuizResultToResDTO)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Named("mapQuizResultToResDTO")
        default QuizResultResDTO mapQuizResultToResDTO(QuizInstance instance) {
        if (instance == null) {
            return null;
        }
        
        return QuizResultResDTO.builder()
                .id(instance.getId())
                .quizTitle(instance.getQuiz() != null ? instance.getQuiz().getTitle() : null)
                .userId(instance.getUser() != null ? instance.getUser().getId() : null)
                .userName(instance.getUser() != null ? instance.getUser().getUserName() : null)
                .score(instance.getScorePercentage())
                .totalQuestions(instance.getTotalPoints())
                .correctAnswers(instance.getEarnedPoints())
                .wrongAnswers(instance.getTotalPoints() - instance.getEarnedPoints())
                .skippedQuestions(instance.getTotalPoints() - instance.getEarnedPoints())
                .completedAt(instance.getSubmittedAt())
                .isPassed(instance.getScorePercentage() >= 70)
                .createdAt(instance.getCreatedAt())
                .updatedAt(instance.getUpdatedAt())
                .isActive(instance.getStatus() == QuizInstanceStatus.SUBMITTED)
                .build();
    }
}