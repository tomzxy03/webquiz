package com.tomzxy.web_quiz.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import com.tomzxy.web_quiz.models.Quiz.QuizInstance;
import com.tomzxy.web_quiz.models.Quiz.QuizUserResponse;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.QuizUserResponseRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuizUserResponseService;

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
    public QuizUserResponseResDTO createUserResponse(QuizUserResponseReqDTO request) {
        log.info("Creating user response for quiz instance: {}", request.getQuizInstanceId());
        
        QuizInstance quizInstance = quizInstanceRepo.findById(request.getQuizInstanceId())
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));
        
        QuizUserResponse response = quizUserResponseMapper.toQuizUserResponse(request);
        response.setQuizInstance(quizInstance);
        response.setAnsweredAt(LocalDateTime.now());
        
        response = quizUserResponseRepo.save(response);
        
        return quizUserResponseMapper.toQuizUserResponseResDTO(response);
    }

    @Override
    public QuizUserResponseResDTO updateUserResponse(Long id, QuizUserResponseReqDTO request) {
        log.info("Updating user response: {}", id);
        
        QuizUserResponse response = quizUserResponseRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User response not found"));
        
        quizUserResponseMapper.updateQuizUserResponseFromDto(request, response);
        response.setUpdatedAt(LocalDateTime.now());
        
        response = quizUserResponseRepo.save(response);
        
        return quizUserResponseMapper.toQuizUserResponseResDTO(response);
    }

    @Override
    public QuizUserResponseResDTO getUserResponseById(Long id) {
        log.info("Getting user response by id: {}", id);
        
        QuizUserResponse response = quizUserResponseRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User response not found"));
        
        return quizUserResponseMapper.toQuizUserResponseResDTO(response);
    }

    @Override
    public void deleteUserResponse(Long id) {
        log.info("Deleting user response: {}", id);
        
        QuizUserResponse response = quizUserResponseRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User response not found"));
        
        response.softDelete();
        quizUserResponseRepo.save(response);
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
    public QuizUserResponseResDTO submitAnswer(Long quizInstanceId, Long userId, Long selectedAnswerId, String userAnswer) {
        log.info("Submitting answer for quiz instance: {}, user: {}", quizInstanceId, userId);
        
        QuizInstance quizInstance = quizInstanceRepo.findById(quizInstanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));
        
        if (!quizInstance.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized for this quiz instance");
        }
        
        QuizUserResponse response = QuizUserResponse.builder()
                .quizInstance(quizInstance)
                .selectedAnswerId(selectedAnswerId)
                .selectedAnswerText(userAnswer)
                .answeredAt(LocalDateTime.now())
                .isSkipped(false)
                .build();
        
        response = quizUserResponseRepo.save(response);
        
        return quizUserResponseMapper.toQuizUserResponseResDTO(response);
    }

    @Override
    public QuizUserResponseResDTO updateAnswer(Long responseId, Long selectedAnswerId, String userAnswer) {
        log.info("Updating answer for response: {}", responseId);
        
        QuizUserResponse response = quizUserResponseRepo.findById(responseId)
                .orElseThrow(() -> new NotFoundException("User response not found"));
        
        response.setSelectedAnswerId(selectedAnswerId);
        response.setSelectedAnswerText(userAnswer);
        response.setUpdatedAt(LocalDateTime.now());
        
        response = quizUserResponseRepo.save(response);
        
        return quizUserResponseMapper.toQuizUserResponseResDTO(response);
    }

    @Override
    public QuizUserResponseResDTO skipQuestion(Long quizInstanceId, Long userId) {
        log.info("Skipping question for quiz instance: {}, user: {}", quizInstanceId, userId);
        
        QuizInstance quizInstance = quizInstanceRepo.findById(quizInstanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));
        
        if (!quizInstance.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized for this quiz instance");
        }
        
        QuizUserResponse response = QuizUserResponse.builder()
                .quizInstance(quizInstance)
                .isSkipped(true)
                .answeredAt(LocalDateTime.now())
                .build();
        
        response = quizUserResponseRepo.save(response);
        
        return quizUserResponseMapper.toQuizUserResponseResDTO(response);
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

    // Validation methods
    @Override
    public boolean canUserSubmitResponse(Long quizInstanceId, Long userId) {
        QuizInstance quizInstance = quizInstanceRepo.findById(quizInstanceId).orElse(null);
        return quizInstance != null && quizInstance.getUser().getId().equals(userId) && quizInstance.isInProgress();
    }

    @Override
    public boolean canUserUpdateResponse(Long responseId, Long userId) {
        QuizUserResponse response = quizUserResponseRepo.findById(responseId).orElse(null);
        return response != null && response.getQuizInstance().getUser().getId().equals(userId);
    }

    @Override
    public boolean hasUserAnsweredQuestion(Long quizInstanceId, Long userId) {
        return !quizUserResponseRepo.findByQuizInstanceIdAndUserId(quizInstanceId, userId).isEmpty();
    }

    @Override
    public boolean isResponseValid(QuizUserResponseReqDTO request) {
        return request != null && request.getQuizInstanceId() != null;
    }

    @Override
    public boolean isTimeExpired(Long quizInstanceId) {
        QuizInstance quizInstance = quizInstanceRepo.findById(quizInstanceId).orElse(null);
        return quizInstance != null && quizInstance.isTimedOut();
    }

    @Override
    public boolean isQuestionSkippable(Long quizInstanceId) {
        return canUserSubmitResponse(quizInstanceId, null);
    }

    @Override
    public boolean isResponseCorrect(Long selectedAnswerId, String userAnswer, Long questionId) {
        // This would need to be implemented based on your specific logic
        return false;
    }

    @Override
    public boolean isResponseTimeEfficient(Integer timeSpentSeconds) {
        return timeSpentSeconds != null && timeSpentSeconds <= 30;
    }

    @Override
    public boolean isResponseTimeConsuming(Integer timeSpentSeconds) {
        return timeSpentSeconds != null && timeSpentSeconds > 120;
    }

    @Override
    public boolean needsReview(Integer timeSpentSeconds) {
        return isResponseTimeEfficient(timeSpentSeconds) || isResponseTimeConsuming(timeSpentSeconds);
    }

    @Override
    public boolean canResubmitResponse(Long responseId) {
        QuizUserResponse response = quizUserResponseRepo.findById(responseId).orElse(null);
        return response != null && response.getQuizInstance().isInProgress();
    }

    @Override
    public boolean isResponseStale(Long responseId, LocalDateTime cutoffDate) {
        QuizUserResponse response = quizUserResponseRepo.findById(responseId).orElse(null);
        return response != null && response.getCreatedAt().isBefore(cutoffDate);
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
