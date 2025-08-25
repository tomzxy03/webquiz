package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.QuizResultReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.models.QuizResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuizResultService {

    // CRUD operations
    QuizResultResDTO createQuizResult(QuizResultReqDTO request);
    
    QuizResultResDTO updateQuizResult(Long id, QuizResultReqDTO request);
    
    QuizResultResDTO getQuizResultById(Long id);
    
    void deleteQuizResult(Long id);
    
    PageResDTO<QuizResultResDTO> getAllQuizResults(int page, int size);

    // Business operations
    QuizResultResDTO submitQuizResult(QuizResultReqDTO request);
    
    QuizResultResDTO calculateAndSaveResult(Long quizInstanceId, Long userId);
    
    List<QuizResultResDTO> getUserResults(Long userId);
    
    PageResDTO<QuizResultResDTO> getUserResults(Long userId, int page, int size);
    
    List<QuizResultResDTO> getQuizInstanceResults(Long quizInstanceId);
    
    List<QuizResultResDTO> getResultsByScoreRange(Double minScore, Double maxScore);
    
    List<QuizResultResDTO> getPassedResults(Boolean isPassed);
    
    List<QuizResultResDTO> getResultsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<QuizResultResDTO> getHighPerformingResults(Double minScore);
    
    List<QuizResultResDTO> getLowPerformingResults(Double maxScore);
    
    List<QuizResultResDTO> getEfficientResults(Integer maxTime);
    
    List<QuizResultResDTO> getSlowResults(Integer minTime);
    
    List<QuizResultResDTO> getRecentResults(LocalDateTime sinceDate);
    
    Optional<QuizResultResDTO> getBestResultForUser(Long userId);
    
    Optional<QuizResultResDTO> getLatestResultForUser(Long userId);
    
    List<QuizResultResDTO> getTopResultsForQuiz(Long quizId, int limit);
    
    List<QuizResultResDTO> getUserProgress(Long userId, LocalDateTime sinceDate);

    // Analytics and statistics
    Double getAverageScoreForUser(Long userId);
    
    Double getAverageTimeForUser(Long userId);
    
    Long getPassedResultsCountForUser(Long userId);
    
    Long getTotalResultsCountForUser(Long userId);
    
    Double getPassRateForUser(Long userId);
    
    Double getAverageScoreForQuiz(Long quizId);
    
    Double getAverageTimeForQuiz(Long quizId);
    
    Long getTotalParticipantsForQuiz(Long quizId);
    
    Double getPassRateForQuiz(Long quizId);

    // Validation and business rules
    boolean canUserSubmitResult(Long quizInstanceId, Long userId);
    
    boolean isResultValid(QuizResultReqDTO request);
    
    boolean hasUserCompletedQuiz(Long quizInstanceId, Long userId);
    
    boolean isTimeExpired(Long quizInstanceId);
    
    boolean canResubmitResult(Long quizInstanceId, Long userId);
} 