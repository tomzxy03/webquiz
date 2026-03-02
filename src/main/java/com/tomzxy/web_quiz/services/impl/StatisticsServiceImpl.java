package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.responses.GlobalStatisticsResDTO;
import com.tomzxy.web_quiz.dto.responses.UserStatisticsResDTO;
import com.tomzxy.web_quiz.repositories.LobbyRepo;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.AttemptService;
import com.tomzxy.web_quiz.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final AttemptService attemptService;
    private final UserRepo userRepo;
    private final QuizRepo quizRepo;
    private final QuizInstanceRepo quizInstanceRepo;
    private final LobbyRepo lobbyRepo;

    @Override
    public UserStatisticsResDTO getUserStatistics(Long userId) {
        return attemptService.getUserStatistics(userId);
    }

    @Override
    public GlobalStatisticsResDTO getGlobalStatistics() {
        long totalUsers = userRepo.count();
        long totalQuizzes = quizRepo.count();
        long totalAttempts = quizInstanceRepo.count();
        long totalGroups = lobbyRepo.count();
        long activeUsers = userRepo.findAllByIsActiveTrue(
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long activeQuizzes = quizRepo.findAllActive(
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        return GlobalStatisticsResDTO.builder()
                .totalUsers(totalUsers)
                .totalQuizzes(totalQuizzes)
                .totalAttempts(totalAttempts)
                .totalGroups(totalGroups)
                .activeUsers(activeUsers)
                .activeQuizzes(activeQuizzes)
                .build();
    }
}
