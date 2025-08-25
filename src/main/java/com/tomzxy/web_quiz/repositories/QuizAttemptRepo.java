package com.tomzxy.web_quiz.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tomzxy.web_quiz.models.Quiz.QuizAttempt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepo extends JpaRepository<QuizAttempt, Long> {

    // Find by user through QuizResult
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.isActive = true")
    List<QuizAttempt> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.isActive = true")
    Page<QuizAttempt> findByUserIdAndIsActiveTrue(@Param("userId") Long userId, Pageable pageable);

    // Find by quiz through QuizResult
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.quiz.id = :quizId AND qa.isActive = true")
    List<QuizAttempt> findByQuizIdAndIsActiveTrue(@Param("quizId") Long quizId);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.quiz.id = :quizId AND qa.isActive = true")
    Page<QuizAttempt> findByQuizIdAndIsActiveTrue(@Param("quizId") Long quizId, Pageable pageable);

    // Find by user and quiz through QuizResult
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.quizResult.quiz.id = :quizId AND qa.isActive = true")
    List<QuizAttempt> findByUserIdAndQuizIdAndIsActiveTrue(@Param("userId") Long userId, @Param("quizId") Long quizId);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.quizResult.quiz.id = :quizId AND qa.isActive = true ORDER BY qa.quizResult.startedAt DESC")
    Optional<QuizAttempt> findFirstByUserIdAndQuizIdAndIsActiveTrueOrderByStartedAtDesc(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // Find by status (using the getStatus() method from QuizAttempt)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.isActive = true AND " +
           "CASE " +
           "WHEN qa.isSkipped = true THEN 'SKIPPED' " +
           "WHEN qa.isCorrect = true THEN 'CORRECT' " +
           "WHEN qa.selectedAnswer IS NOT NULL AND qa.selectedAnswer != '' THEN 'INCORRECT' " +
           "ELSE 'NOT_ANSWERED' " +
           "END = :status")
    List<QuizAttempt> findByStatusAndIsActiveTrue(@Param("status") String status);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.isActive = true AND " +
           "CASE " +
           "WHEN qa.isSkipped = true THEN 'SKIPPED' " +
           "WHEN qa.isCorrect = true THEN 'CORRECT' " +
           "WHEN qa.selectedAnswer IS NOT NULL AND qa.selectedAnswer != '' THEN 'INCORRECT' " +
           "ELSE 'NOT_ANSWERED' " +
           "END = :status")
    Page<QuizAttempt> findByStatusAndIsActiveTrue(@Param("status") String status, Pageable pageable);

    // Find in progress attempts (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.isCompleted = false AND qa.isActive = true")
    List<QuizAttempt> findInProgressAttempts();

    // Find completed attempts (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.isCompleted = true AND qa.isActive = true")
    List<QuizAttempt> findCompletedAttempts();

    // Find abandoned attempts (through QuizResult - assuming abandoned means not completed and old)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.isCompleted = false AND qa.quizResult.startedAt < :cutoffDate AND qa.isActive = true")
    List<QuizAttempt> findAbandonedAttempts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find by date range (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.startedAt BETWEEN :startDate AND :endDate AND qa.isActive = true")
    List<QuizAttempt> findByStartedDateRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.completedAt BETWEEN :startDate AND :endDate AND qa.isActive = true")
    List<QuizAttempt> findByCompletedDateRange(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);

    // Find recent attempts (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.startedAt >= :sinceDate AND qa.isActive = true")
    List<QuizAttempt> findRecentAttempts(@Param("sinceDate") LocalDateTime sinceDate);

    // Find long-running attempts (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.startedAt < :cutoffDate AND qa.quizResult.isCompleted = false AND qa.isActive = true")
    List<QuizAttempt> findLongRunningAttempts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find first attempt for user and quiz (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.quizResult.quiz.id = :quizId AND qa.isActive = true ORDER BY qa.quizResult.startedAt ASC")
    Optional<QuizAttempt> findFirstAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // Find latest attempt for user and quiz (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.quizResult.quiz.id = :quizId AND qa.isActive = true ORDER BY qa.quizResult.startedAt DESC")
    Optional<QuizAttempt> findLatestAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // Count attempts by user (through QuizResult)
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.isActive = true")
    long countByUserIdAndIsActiveTrue(@Param("userId") Long userId);

    // Count attempts by quiz (through QuizResult)
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quizResult.quiz.id = :quizId AND qa.isActive = true")
    long countByQuizIdAndIsActiveTrue(@Param("quizId") Long quizId);

    // Count attempts by status
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.isActive = true AND " +
           "CASE " +
           "WHEN qa.isSkipped = true THEN 'SKIPPED' " +
           "WHEN qa.isCorrect = true THEN 'CORRECT' " +
           "WHEN qa.selectedAnswer IS NOT NULL AND qa.selectedAnswer != '' THEN 'INCORRECT' " +
           "ELSE 'NOT_ANSWERED' " +
           "END = :status")
    long countByStatusAndIsActiveTrue(@Param("status") String status);

    // Check if user has active attempt (through QuizResult)
    @Query("SELECT COUNT(qa) > 0 FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.quizResult.quiz.id = :quizId AND qa.quizResult.isCompleted = false AND qa.isActive = true")
    boolean hasActiveAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // Find attempts that can be resumed (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.quizResult.isCompleted = false AND qa.isActive = true")
    List<QuizAttempt> findResumableAttempts(@Param("userId") Long userId);

    // Find attempts for specific quiz instance (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.quiz.id = :quizInstanceId AND qa.isActive = true")
    List<QuizAttempt> findByQuizInstanceId(@Param("quizInstanceId") Long quizInstanceId);

    // Find user's attempt history (through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.user.id = :userId AND qa.isActive = true ORDER BY qa.quizResult.startedAt DESC")
    List<QuizAttempt> findUserAttemptHistory(@Param("userId") Long userId, Pageable pageable);

    // Find attempts with specific progress (this might need adjustment based on your domain)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.isActive = true")
    List<QuizAttempt> findByMinProgress(@Param("minProgress") Integer minProgress);

    // Find attempts that need cleanup (old abandoned through QuizResult)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizResult.isCompleted = false AND qa.quizResult.startedAt < :cutoffDate AND qa.isActive = true")
    List<QuizAttempt> findAbandonedAttemptsForCleanup(@Param("cutoffDate") LocalDateTime cutoffDate);
} 