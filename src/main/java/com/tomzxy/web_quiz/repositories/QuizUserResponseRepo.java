package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.QuizUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizUserResponseRepo extends JpaRepository<QuizUserResponse, Long> {

    // Find by user
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    List<QuizUserResponse> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);
    
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    Page<QuizUserResponse> findByUserIdAndIsActiveTrue(@Param("userId") Long userId, Pageable pageable);

    // Find by quiz instance question
    List<QuizUserResponse> findByQuizInstanceQuestionIdAndIsActiveTrue(Long quizInstanceQuestionId);
    
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.id = :quizInstanceQuestionId AND qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    Optional<QuizUserResponse> findByQuizInstanceQuestionIdAndUserIdAndIsActiveTrue(@Param("quizInstanceQuestionId") Long quizInstanceQuestionId, @Param("userId") Long userId);

    // Find by quiz instance
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.id = :quizInstanceId AND qur.isActive = true")
    List<QuizUserResponse> findByQuizInstanceId(@Param("quizInstanceId") Long quizInstanceId);

    // Find by quiz instance and user
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.id = :quizInstanceId AND qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    List<QuizUserResponse> findByQuizInstanceIdAndUserId(@Param("quizInstanceId") Long quizInstanceId, @Param("userId") Long userId);

    // Find by quiz instance and quiz instance question
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.id = :quizInstanceId AND qur.quizInstanceQuestion.id = :quizInstanceQuestionId AND qur.isActive = true")
    Optional<QuizUserResponse> findByQuizInstanceIdAndQuizInstanceQuestionId(@Param("quizInstanceId") Long quizInstanceId, @Param("quizInstanceQuestionId") Long quizInstanceQuestionId);

    // Find correct responses
    List<QuizUserResponse> findByIsCorrectAndIsActiveTrue(Boolean isCorrect);
    
    Page<QuizUserResponse> findByIsCorrectAndIsActiveTrue(Boolean isCorrect, Pageable pageable);

    // Find by time spent range
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.responseTimeSeconds BETWEEN :minTime AND :maxTime AND qur.isActive = true")
    List<QuizUserResponse> findByTimeSpentRange(@Param("minTime") Integer minTime, @Param("maxTime") Integer maxTime);

    // Find fast responses
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.responseTimeSeconds <= :maxTime AND qur.isActive = true")
    List<QuizUserResponse> findFastResponses(@Param("maxTime") Integer maxTime);

    // Find slow responses
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.responseTimeSeconds > :minTime AND qur.isActive = true")
    List<QuizUserResponse> findSlowResponses(@Param("minTime") Integer minTime);

    // Find by answer date range
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.answeredAt BETWEEN :startDate AND :endDate AND qur.isActive = true")
    List<QuizUserResponse> findByAnsweredDateRange(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    // Find recent responses
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.answeredAt >= :sinceDate AND qur.isActive = true")
    List<QuizUserResponse> findRecentResponses(@Param("sinceDate") LocalDateTime sinceDate);

    @Query("SELECT qur FROM QuizUserResponse qur " +
       "WHERE qur.selectedAnswerId IS NULL " +
       "AND qur.selectedAnswerText IS NULL " +
       "AND qur.isActive = true")
    List<QuizUserResponse> findSkippedQuestions();

    // Find answered questions
    @Query("SELECT qur FROM QuizUserResponse qur WHERE (qur.selectedAnswerId IS NOT NULL OR qur.selectedAnswerText IS NOT NULL) AND qur.isActive = true")
    List<QuizUserResponse> findAnsweredQuestions();

    // Find responses for specific question
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.originalQuestion.id = :questionId AND qur.isActive = true")
    List<QuizUserResponse> findByOriginalQuestionId(@Param("questionId") Long questionId);

    // Find user's response history
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true ORDER BY qur.answeredAt DESC")
    List<QuizUserResponse> findUserResponseHistory(@Param("userId") Long userId, Pageable pageable);

    // Count by user
    @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    long countByUserIdAndIsActiveTrue(@Param("userId") Long userId);

    // Count by quiz instance question
    long countByQuizInstanceQuestionIdAndIsActiveTrue(Long quizInstanceQuestionId);

    // Count correct vs incorrect
    long countByIsCorrectAndIsActiveTrue(Boolean isCorrect);

    // Count by quiz instance
    @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.id = :quizInstanceId AND qur.isActive = true")
    long countByQuizInstanceId(@Param("quizInstanceId") Long quizInstanceId);

    // Count by quiz instance and user
    @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.id = :quizInstanceId AND qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    long countByQuizInstanceIdAndUserId(@Param("quizInstanceId") Long quizInstanceId, @Param("userId") Long userId);

    // Custom analytics queries
    @Query("SELECT AVG(qur.responseTimeSeconds) FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    Optional<Double> getAverageTimeForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isCorrect = true AND qur.isActive = true")
    long countCorrectResponsesForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.isActive = true")
    long countTotalResponsesForUser(@Param("userId") Long userId);

    // Find user's performance on specific question
    @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstanceQuestion.quizInstance.user.id = :userId AND qur.quizInstanceQuestion.originalQuestion.id = :questionId AND qur.isActive = true ORDER BY qur.answeredAt DESC")
    List<QuizUserResponse> findUserPerformanceOnQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    // Find responses that need review (very fast or very slow)
    @Query("SELECT qur FROM QuizUserResponse qur WHERE (qur.responseTimeSeconds < 5 OR qur.responseTimeSeconds > 300) AND qur.isActive = true")
    List<QuizUserResponse> findResponsesNeedingReview();
} 