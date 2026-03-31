package com.tomzxy.web_quiz.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomzxy.web_quiz.dto.requests.QuizUserResponseReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizUserResponseResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.QuizUserResponseMapper;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.QuizUserResponseRepo;
import com.tomzxy.web_quiz.services.QuizUserResponseService;
import com.tomzxy.web_quiz.services.common.ConvertToPageResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizUserResponseServiceImpl implements QuizUserResponseService {

    private final QuizUserResponseRepo quizUserResponseRepo;
    private final QuizInstanceRepo quizInstanceRepo;
    private final QuizUserResponseMapper quizUserResponseMapper;
    private final ConvertToPageResDTO convertToPageResDTO;

    @Override
    public QuizUserResponseResDTO getUserResponseById(Long id) {
        log.info("Getting user response by id: {}", id);
        
        QuizUserResponse response = quizUserResponseRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User response not found"));
        
        return quizUserResponseMapper.toQuizUserResponseResDTO(response);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageResDTO<QuizUserResponseResDTO> getAllUserResponses(int page, int size) {
        log.info("Getting all user responses with pagination: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizUserResponse> responses = quizUserResponseRepo.findAll(pageable);
        
        PageResDTO<QuizUserResponseResDTO> result = convertToPageResDTO.convertPageResponse(responses, pageable, quizUserResponseMapper::toQuizUserResponseResDTO);
        return (PageResDTO<QuizUserResponseResDTO>) (PageResDTO<?>) result;
    }

    @Override
    public List<QuizUserResponseResDTO> getUserResponses(Long userId) {
        log.info("Getting user responses for user: {}", userId);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findByUserIdAndIsActiveTrue(userId);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageResDTO<QuizUserResponseResDTO> getUserResponses(Long userId, int page, int size) {
        log.info("Getting user responses for user: {} with pagination: page={}, size={}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizUserResponse> responses = quizUserResponseRepo.findByUserIdAndIsActiveTrue(userId, pageable);
        
        PageResDTO<QuizUserResponseResDTO> result = convertToPageResDTO.convertPageResponse(responses, pageable, quizUserResponseMapper::toQuizUserResponseResDTO);
        return (PageResDTO<QuizUserResponseResDTO>) (PageResDTO<?>) result;
    }

    @Override
    public List<QuizUserResponseResDTO> getQuestionResponses(Long questionId) {
        log.info("Getting question responses for question: {}", questionId);
        
        // This would need to be implemented based on your specific requirements
        return List.of();
    }

    @Override
    public List<QuizUserResponseResDTO> getQuizInstanceResponses(Long quizInstanceId) {
        log.info("Getting quiz instance responses for quiz instance: {}", quizInstanceId);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findByQuizInstanceId(quizInstanceId);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getUserQuizInstanceResponses(Long quizInstanceId, Long userId) {
        log.info("Getting user quiz instance responses for quiz instance: {}, user: {}", quizInstanceId, userId);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findByQuizInstanceIdAndUserId(quizInstanceId, userId);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getCorrectResponses(Boolean isCorrect) {
        log.info("Getting {} responses", isCorrect ? "correct" : "incorrect");
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findByIsCorrectAndIsActiveTrue(isCorrect);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageResDTO<QuizUserResponseResDTO> getCorrectResponses(Boolean isCorrect, int page, int size) {
        log.info("Getting {} responses with pagination: page={}, size={}", isCorrect ? "correct" : "incorrect", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizUserResponse> responses = quizUserResponseRepo.findByIsCorrectAndIsActiveTrue(isCorrect, pageable);
        
        return convertToPageResDTO.convertPageResponse(responses, pageable, quizUserResponseMapper::toQuizUserResponseResDTO);
    }

    @Override
    public List<QuizUserResponseResDTO> getResponsesByTimeRange(Integer minTime, Integer maxTime) {
        log.info("Getting responses by time range: {} - {}", minTime, maxTime);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findByTimeSpentRange(minTime, maxTime);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getFastResponses(Integer maxTime) {
        log.info("Getting fast responses with max time: {}", maxTime);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findFastResponses(maxTime);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getSlowResponses(Integer minTime) {
        log.info("Getting slow responses with min time: {}", minTime);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findSlowResponses(minTime);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getResponsesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting responses by date range: {} - {}", startDate, endDate);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findByAnsweredDateRange(startDate, endDate);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getRecentResponses(LocalDateTime sinceDate) {
        log.info("Getting recent responses since: {}", sinceDate);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findRecentResponses(sinceDate);
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getSkippedQuestions() {
        log.info("Getting skipped questions");
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findSkippedQuestions();
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getAnsweredQuestions() {
        log.info("Getting answered questions");
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findAnsweredQuestions();
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    // Additional methods with simplified implementations
    @Override
    public List<QuizUserResponseResDTO> getResponsesForOriginalQuestion(Long questionId) {
        // This would need to be implemented based on your specific requirements
        return List.of();
    }

    @Override
    public List<QuizUserResponseResDTO> getUserResponseHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<QuizUserResponse> responses = quizUserResponseRepo.findUserResponseHistory(userId, pageable);
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    @Override
    public List<QuizUserResponseResDTO> getUserPerformanceOnQuestion(Long userId, Long questionId) {
        // This would need to be implemented based on your specific requirements
        return List.of();
    }

    @Override
    public List<QuizUserResponseResDTO> getResponsesNeedingReview() {
        log.info("Getting responses needing review");
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findResponsesNeedingReview();
        
        return quizUserResponseMapper.toQuizUserResponseResDTOList(responses);
    }

    // Analytics methods
    @Override
    public Long getResponseCountForUser(Long userId) {
        return quizUserResponseRepo.countByUserIdAndIsActiveTrue(userId);
    }

    @Override
    public Long getCorrectResponseCount(Boolean isCorrect) {
        return quizUserResponseRepo.countByIsCorrectAndIsActiveTrue(isCorrect);
    }

    @Override
    public Long getResponseCountForQuizInstance(Long quizInstanceId) {
        return quizUserResponseRepo.countByQuizInstanceId(quizInstanceId);
    }

    @Override
    public Long getUserQuizInstanceResponseCount(Long quizInstanceId, Long userId) {
        return quizUserResponseRepo.countByQuizInstanceIdAndUserId(quizInstanceId, userId);
    }

    @Override
    public Double getAverageTimeForUser(Long userId) {
        return quizUserResponseRepo.getAverageTimeForUser(userId).orElse(0.0);
    }

    @Override
    public Long getCorrectResponseCountForUser(Long userId) {
        return quizUserResponseRepo.countCorrectResponsesForUser(userId);
    }

    @Override
    public Long getTotalResponseCountForUser(Long userId) {
        return quizUserResponseRepo.countTotalResponsesForUser(userId);
    }

    @Override
    public Double getAccuracyRateForUser(Long userId) {
        long total = getTotalResponseCountForUser(userId);
        if (total == 0) return 0.0;
        long correct = getCorrectResponseCountForUser(userId);
        return (double) correct / total * 100;
    }

    @Override
    public Long getSkippedQuestionCount(Long userId) {
        return quizUserResponseRepo.findByUserIdAndIsActiveTrue(userId).stream()
                .mapToLong(r -> r.isSkipped() ? 1 : 0)
                .sum();
    }

    @Override
    public Long getAnsweredQuestionCount(Long userId) {
        return quizUserResponseRepo.findByUserIdAndIsActiveTrue(userId).stream()
                .mapToLong(r -> !r.isSkipped() ? 1 : 0)
                .sum();
    }

    // Additional methods that need implementation
    @Override
    public Long getResponseCountForQuestion(Long quizInstanceQuestionId) {
        return 0L; // Not applicable with current model
    }

    @Override
    public Double getAverageTimeForQuestion(Long quizInstanceQuestionId) {
        return 0.0; // Not applicable with current model
    }

    @Override
    public Double getAccuracyRateForQuestion(Long quizInstanceQuestionId) {
        return 0.0; // Not applicable with current model
    }

    @Override
    public Double getAverageTimeForQuizInstance(Long quizInstanceId) {
        List<QuizUserResponse> responses = quizUserResponseRepo.findByQuizInstanceId(quizInstanceId);
        return responses.stream()
                .filter(r -> r.getResponseTimeSeconds() != null)
                .mapToInt(QuizUserResponse::getResponseTimeSeconds)
                .average()
                .orElse(0.0);
    }
}
