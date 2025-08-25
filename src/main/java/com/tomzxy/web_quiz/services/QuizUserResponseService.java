package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.QuizUserResponseReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizUserResponseResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuizUserResponseService {

    // CRUD operations
    QuizUserResponseResDTO createUserResponse(QuizUserResponseReqDTO request);
    
    QuizUserResponseResDTO updateUserResponse(Long id, QuizUserResponseReqDTO request);
    
    QuizUserResponseResDTO getUserResponseById(Long id);
    
    void deleteUserResponse(Long id);
    
    PageResDTO<QuizUserResponseResDTO> getAllUserResponses(int page, int size);

    // Business operations
    QuizUserResponseResDTO submitAnswer(Long quizInstanceQuestionId, Long userId, Long selectedAnswerId, String userAnswer);
    
    QuizUserResponseResDTO updateAnswer(Long responseId, Long selectedAnswerId, String userAnswer);
    
    QuizUserResponseResDTO skipQuestion(Long quizInstanceQuestionId, Long userId);
    
    List<QuizUserResponseResDTO> getUserResponses(Long userId);
    
    PageResDTO<QuizUserResponseResDTO> getUserResponses(Long userId, int page, int size);
    
    List<QuizUserResponseResDTO> getQuestionResponses(Long quizInstanceQuestionId);
    
    List<QuizUserResponseResDTO> getQuizInstanceResponses(Long quizInstanceId);
    
    List<QuizUserResponseResDTO> getUserQuizInstanceResponses(Long quizInstanceId, Long userId);
    
    List<QuizUserResponseResDTO> getCorrectResponses(Boolean isCorrect);
    
    PageResDTO<QuizUserResponseResDTO> getCorrectResponses(Boolean isCorrect, int page, int size);
    
    List<QuizUserResponseResDTO> getResponsesByTimeRange(Integer minTime, Integer maxTime);
    
    List<QuizUserResponseResDTO> getFastResponses(Integer maxTime);
    
    List<QuizUserResponseResDTO> getSlowResponses(Integer minTime);
    
    List<QuizUserResponseResDTO> getResponsesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<QuizUserResponseResDTO> getRecentResponses(LocalDateTime sinceDate);
    
    List<QuizUserResponseResDTO> getSkippedQuestions();
    
    List<QuizUserResponseResDTO> getAnsweredQuestions();
    
    List<QuizUserResponseResDTO> getResponsesForOriginalQuestion(Long questionId);
    
    List<QuizUserResponseResDTO> getUserResponseHistory(Long userId, int page, int size);
    
    List<QuizUserResponseResDTO> getUserPerformanceOnQuestion(Long userId, Long questionId);
    
    List<QuizUserResponseResDTO> getResponsesNeedingReview();

    // Analytics and statistics
    Long getResponseCountForUser(Long userId);
    
    Long getResponseCountForQuestion(Long quizInstanceQuestionId);
    
    Long getCorrectResponseCount(Boolean isCorrect);
    
    Long getResponseCountForQuizInstance(Long quizInstanceId);
    
    Long getUserQuizInstanceResponseCount(Long quizInstanceId, Long userId);
    
    Double getAverageTimeForUser(Long userId);
    
    Double getAverageTimeForQuestion(Long quizInstanceQuestionId);
    
    Long getCorrectResponseCountForUser(Long userId);
    
    Long getTotalResponseCountForUser(Long userId);
    
    Double getAccuracyRateForUser(Long userId);
    
    Double getAccuracyRateForQuestion(Long quizInstanceQuestionId);
    
    Double getAverageTimeForQuizInstance(Long quizInstanceId);
    
    Long getSkippedQuestionCount(Long userId);
    
    Long getAnsweredQuestionCount(Long userId);

    // Validation and business rules
    boolean canUserSubmitResponse(Long quizInstanceQuestionId, Long userId);
    
    boolean canUserUpdateResponse(Long responseId, Long userId);
    
    boolean hasUserAnsweredQuestion(Long quizInstanceQuestionId, Long userId);
    
    boolean isResponseValid(QuizUserResponseReqDTO request);
    
    boolean isTimeExpired(Long quizInstanceQuestionId);
    
    boolean isQuestionSkippable(Long quizInstanceQuestionId);
    
    boolean isResponseCorrect(Long selectedAnswerId, String userAnswer, Long questionId);
    
    boolean isResponseTimeEfficient(Integer timeSpentSeconds);
    
    boolean isResponseTimeConsuming(Integer timeSpentSeconds);
    
    boolean needsReview(Integer timeSpentSeconds);
    
    boolean canResubmitResponse(Long responseId);
    
    boolean isResponseStale(Long responseId, LocalDateTime cutoffDate);
} 