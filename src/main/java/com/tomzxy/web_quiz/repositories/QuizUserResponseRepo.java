package com.tomzxy.web_quiz.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tomzxy.web_quiz.dto.responses.AttemptResDTO;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizUserResponseRepo extends JpaRepository<QuizUserResponse, Long> {

        // Find by user
        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstance.user.id = :userId AND qur.isActive = true")
        List<QuizUserResponse> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);

        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstance.user.id = :userId AND qur.isActive = true")
        Page<QuizUserResponse> findByUserIdAndIsActiveTrue(@Param("userId") Long userId, Pageable pageable);

        // Find by quiz instance
        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstance.id = :quizInstanceId AND qur.isActive = true")
        List<QuizUserResponse> findByQuizInstanceId(@Param("quizInstanceId") Long quizInstanceId);

        // Find by quiz instance and user
        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstance.id = :quizInstanceId AND qur.quizInstance.user.id = :userId AND qur.isActive = true")
        List<QuizUserResponse> findByQuizInstanceIdAndUserId(@Param("quizInstanceId") Long quizInstanceId,
                        @Param("userId") Long userId);

        // Find correct responses
        List<QuizUserResponse> findByIsCorrectAndIsActiveTrue(Boolean isCorrect);

        Page<QuizUserResponse> findByIsCorrectAndIsActiveTrue(Boolean isCorrect, Pageable pageable);

        // Find by time spent range
        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.responseTimeSeconds BETWEEN :minTime AND :maxTime AND qur.isActive = true")
        List<QuizUserResponse> findByTimeSpentRange(@Param("minTime") Integer minTime,
                        @Param("maxTime") Integer maxTime);

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

        // Find skipped questions
        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.isSkipped = true AND qur.isActive = true")
        List<QuizUserResponse> findSkippedQuestions();

        // Find answered questions
        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.isSkipped = false AND qur.isActive = true")
        List<QuizUserResponse> findAnsweredQuestions();

        // Find user's response history
        @Query("SELECT qur FROM QuizUserResponse qur WHERE qur.quizInstance.user.id = :userId AND qur.isActive = true ORDER BY qur.answeredAt DESC")
        List<QuizUserResponse> findUserResponseHistory(@Param("userId") Long userId, Pageable pageable);

        // Count by user
        @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstance.user.id = :userId AND qur.isActive = true")
        long countByUserIdAndIsActiveTrue(@Param("userId") Long userId);

        // Count correct vs incorrect
        long countByIsCorrectAndIsActiveTrue(Boolean isCorrect);

        // Count by quiz instance
        @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstance.id = :quizInstanceId AND qur.isActive = true")
        long countByQuizInstanceId(@Param("quizInstanceId") Long quizInstanceId);

        // Count by quiz instance and user
        @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstance.id = :quizInstanceId AND qur.quizInstance.user.id = :userId AND qur.isActive = true")
        long countByQuizInstanceIdAndUserId(@Param("quizInstanceId") Long quizInstanceId, @Param("userId") Long userId);

        // Custom analytics queries
        @Query("SELECT AVG(qur.responseTimeSeconds) FROM QuizUserResponse qur WHERE qur.quizInstance.user.id = :userId AND qur.isActive = true")
        Optional<Double> getAverageTimeForUser(@Param("userId") Long userId);

        @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstance.user.id = :userId AND qur.isCorrect = true AND qur.isActive = true")
        long countCorrectResponsesForUser(@Param("userId") Long userId);

        @Query("SELECT COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstance.user.id = :userId AND qur.isActive = true")
        long countTotalResponsesForUser(@Param("userId") Long userId);

        // Find responses that need review (very fast or very slow)
        @Query("SELECT qur FROM QuizUserResponse qur WHERE (qur.responseTimeSeconds < 5 OR qur.responseTimeSeconds > 300) AND qur.isActive = true")
        List<QuizUserResponse> findResponsesNeedingReview();

        @Modifying
        @Query("DELETE FROM QuizUserResponse r WHERE r.quizInstance.id IN (" +
                        "SELECT i.id FROM QuizInstance i WHERE i.guestId IS NOT NULL AND (" +
                        "(i.status = 'IN_PROGRESS' AND i.updatedAt < :abandonedTime) OR " +
                        "(i.status IN ('SUBMITTED', 'COMPLETED') AND i.updatedAt < :completedTime)))")
        int deleteResponsesByGuestCriteria(
                        @Param("abandonedTime") LocalDateTime abandonedTime,
                        @Param("completedTime") LocalDateTime completedTime);

        Collection<AttemptResDTO> countCorrectByQuizInstanceIdIn(List<Long> instanceIds);

        @Query("SELECT qur.quizInstance.id, COUNT(qur) FROM QuizUserResponse qur WHERE qur.quizInstance.id IN :quizInstanceIds AND qur.isActive = true GROUP BY qur.quizInstance.id")
        List<Object[]> countByQuizInstanceIdIn(@Param("quizInstanceIds") List<Long> quizInstanceIds);
}
