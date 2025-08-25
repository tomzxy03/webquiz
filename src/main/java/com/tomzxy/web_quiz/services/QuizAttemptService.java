package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.QuizAttemptReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizAttemptResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuizAttemptService {

    // CRUD operations
    QuizAttemptResDTO createQuizAttempt(QuizAttemptReqDTO request);
    
    QuizAttemptResDTO updateQuizAttempt(Long id, QuizAttemptReqDTO request);
    
    QuizAttemptResDTO getQuizAttemptById(Long id);
    
    void deleteQuizAttempt(Long id);
    
    PageResDTO<QuizAttemptResDTO> getAllQuizAttempts(int page, int size);

    // Business operations
    QuizAttemptResDTO startQuizAttempt(Long quizId, Long userId);
    
    QuizAttemptResDTO resumeQuizAttempt(Long attemptId);
    
    QuizAttemptResDTO updateAttemptProgress(Long attemptId, Integer currentQuestionIndex, String answersSnapshot);
    
    QuizAttemptResDTO completeQuizAttempt(Long attemptId);
    
    QuizAttemptResDTO abandonQuizAttempt(Long attemptId);
    
    List<QuizAttemptResDTO> getUserAttempts(Long userId);
    
    PageResDTO<QuizAttemptResDTO> getUserAttempts(Long userId, int page, int size);
    
    List<QuizAttemptResDTO> getQuizAttempts(Long quizId);
    
    List<QuizAttemptResDTO> getUserQuizAttempts(Long userId, Long quizId);
    
    List<QuizAttemptResDTO> getAttemptsByStatus(String status);
    
    PageResDTO<QuizAttemptResDTO> getAttemptsByStatus(String status, int page, int size);
    
    List<QuizAttemptResDTO> getInProgressAttempts();
    
    List<QuizAttemptResDTO> getCompletedAttempts();
    
    List<QuizAttemptResDTO> getAbandonedAttempts();
    
    List<QuizAttemptResDTO> getAttemptsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<QuizAttemptResDTO> getRecentAttempts(LocalDateTime sinceDate);
    
    List<QuizAttemptResDTO> getLongRunningAttempts(LocalDateTime cutoffDate);
    
    Optional<QuizAttemptResDTO> getFirstAttempt(Long userId, Long quizId);
    
    Optional<QuizAttemptResDTO> getLatestAttempt(Long userId, Long quizId);
    
    List<QuizAttemptResDTO> getResumableAttempts(Long userId);
    
    List<QuizAttemptResDTO> getAttemptsForQuizInstance(Long quizInstanceId);
    
    List<QuizAttemptResDTO> getUserAttemptHistory(Long userId, int page, int size);
    
    List<QuizAttemptResDTO> getAttemptsByMinProgress(Integer minProgress);
    
    List<QuizAttemptResDTO> getAbandonedAttemptsForCleanup(LocalDateTime cutoffDate);

    // Analytics and statistics
    Long getAttemptCountForUser(Long userId);
    
    Long getAttemptCountForQuiz(Long quizId);
    
    Long getAttemptCountByStatus(String status);
    
    Long getUserQuizAttemptCount(Long userId, Long quizId);
    
    Double getAverageAttemptDuration(Long userId);
    
    Double getAverageAttemptDurationForQuiz(Long quizId);
    
    Integer getMaxAttemptsForUser(Long userId, Long quizId);
    
    Double getCompletionRateForUser(Long userId);
    
    Double getCompletionRateForQuiz(Long quizId);

    // Validation and business rules
    boolean canUserStartAttempt(Long quizId, Long userId);
    
    boolean canUserResumeAttempt(Long attemptId, Long userId);
    
    boolean hasActiveAttempt(Long userId, Long quizId);
    
    boolean isAttemptInProgress(Long attemptId);
    
    boolean isAttemptCompleted(Long attemptId);
    
    boolean isAttemptAbandoned(Long attemptId);
    
    boolean isTimeExpired(Long attemptId);
    
    boolean canSubmitAttempt(Long attemptId);
    
    boolean isAttemptValid(QuizAttemptReqDTO request);
    
    boolean hasReachedMaxAttempts(Long userId, Long quizId);
    
    boolean isAttemptStale(Long attemptId, LocalDateTime cutoffDate);
    
    boolean needsCleanup(Long attemptId);
} 