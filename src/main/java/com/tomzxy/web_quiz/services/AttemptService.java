package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.CreateAttemptReqDTO;
import com.tomzxy.web_quiz.dto.responses.AttemptDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.AttemptResDTO;
import com.tomzxy.web_quiz.dto.responses.UserStatisticsResDTO;

import java.util.List;

public interface AttemptService {
    List<AttemptResDTO> getAllAttempts(Long userId, Long quizId);
    AttemptDetailResDTO getAttemptById(Long id);
    List<AttemptResDTO> getAttemptsByUser(Long userId);
    List<AttemptResDTO> getAttemptsByQuizAndUser(Long quizId, Long userId);
    AttemptDetailResDTO createAttempt(CreateAttemptReqDTO request);
    void deleteAttempt(Long id);
    UserStatisticsResDTO getUserStatistics(Long userId);
}
