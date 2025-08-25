package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.QuizResult;
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
public interface QuizResultRepo extends JpaRepository<QuizResult, Long> {

    // Find by user
    List<QuizResult> findByUserIdAndIsActiveTrue(Long userId);
    
    Page<QuizResult> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);

    // Find by quiz (not quiz instance)
    List<QuizResult> findByQuizIdAndIsActiveTrue(Long quizId);
    
    Optional<QuizResult> findByQuizIdAndUserIdAndIsActiveTrue(Long quizId, Long userId);

    // Find by score range
    @Query("SELECT qr FROM QuizResult qr WHERE qr.score BETWEEN :minScore AND :maxScore AND qr.isActive = true")
    List<QuizResult> findByScoreRange(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    // Find passed results
    List<QuizResult> findByIsPassedAndIsActiveTrue(Boolean isPassed);
    
    Page<QuizResult> findByIsPassedAndIsActiveTrue(Boolean isPassed, Pageable pageable);

    // Find by completion date range
    @Query("SELECT qr FROM QuizResult qr WHERE qr.completedAt BETWEEN :startDate AND :endDate AND qr.isActive = true")
    List<QuizResult> findByCompletionDateRange(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);

    // Find high performing results
    @Query("SELECT qr FROM QuizResult qr WHERE qr.score >= :minScore AND qr.isActive = true")
    List<QuizResult> findHighPerformingResults(@Param("minScore") Integer minScore);

    // Find low performing results
    @Query("SELECT qr FROM QuizResult qr WHERE qr.score < :maxScore AND qr.isActive = true")
    List<QuizResult> findLowPerformingResults(@Param("maxScore") Integer maxScore);

    // Find efficient results (fast completion) - using completionTimeMinutes
    @Query("SELECT qr FROM QuizResult qr WHERE qr.completionTimeMinutes <= :maxTime AND qr.isActive = true")
    List<QuizResult> findEfficientResults(@Param("maxTime") Integer maxTime);

    // Find slow results - using completionTimeMinutes
    @Query("SELECT qr FROM QuizResult qr WHERE qr.completionTimeMinutes > :minTime AND qr.isActive = true")
    List<QuizResult> findSlowResults(@Param("minTime") Integer minTime);

    // Find recent results
    @Query("SELECT qr FROM QuizResult qr WHERE qr.completedAt >= :sinceDate AND qr.isActive = true")
    List<QuizResult> findRecentResults(@Param("sinceDate") LocalDateTime sinceDate);

    // Find best result for user
    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.isActive = true ORDER BY qr.score DESC, qr.completionTimeMinutes ASC")
    Optional<QuizResult> findBestResultForUser(@Param("userId") Long userId);

    // Find latest result for user
    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.isActive = true ORDER BY qr.completedAt DESC")
    Optional<QuizResult> findLatestResultForUser(@Param("userId") Long userId);

    // Count by user
    long countByUserIdAndIsActiveTrue(Long userId);

    // Count passed vs failed
    long countByIsPassedAndIsActiveTrue(Boolean isPassed);

    // Custom analytics queries
    @Query("SELECT AVG(qr.score) " +
            "FROM QuizResult qr " +
       "JOIN qr.user u " +
       "WHERE u.id = :userId " + 
       "AND qr.isActive = true")
    Optional<Double> getAverageScoreForUser(@Param("userId") Long userId);

    @Query("SELECT AVG(qr.completionTimeMinutes) " +
       "FROM QuizResult qr " +
            "JOIN qr.user u " +
            "WHERE u.id = :userId " +
            "AND qr.isActive = true")
    Optional<Double> getAverageTimeForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(qr) " +
            "FROM QuizResult qr " +
            "JOIN qr.user u " +
            "WHERE u.id = :userId " +
            "AND qr.isPassed = true " +
            "AND qr.isActive = true")
    long countPassedResultsForUser(@Param("userId") Long userId);

    @Query("SELECT qr FROM QuizResult qr " +
            "JOIN qr.user u " +
            "WHERE u.id = :userId " +
            "AND qr.isActive = true " +
            "ORDER BY qr.completedAt DESC")
    List<QuizResult> findUserResultHistory(@Param("userId") Long userId, Pageable pageable);

    // Find results for specific quiz
    @Query("SELECT qr FROM QuizResult qr " +
       "JOIN qr.quiz q " +
       "WHERE q.id = :quizId " +
       "AND qr.isActive = true")
    List<QuizResult> findByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT qr FROM QuizResult qr " +
            "JOIN qr.quiz q " +
            "WHERE q.id = :quizId " +
            "AND qr.isActive = true " +
            "ORDER BY qr.score DESC")
    List<QuizResult> findTopResultsForQuiz(@Param("quizId") Long quizId, Pageable pageable);

    // Find improvement over time
    @Query("SELECT qr FROM QuizResult qr " +
            "JOIN qr.user u " +
            "WHERE u.id = :userId " +
            "AND qr.completedAt >= :sinceDate " +
            "AND qr.isActive = true " +
            "ORDER BY qr.completedAt ASC")
    List<QuizResult> findUserProgress(@Param("userId") Long userId, @Param("sinceDate") LocalDateTime sinceDate);
} 