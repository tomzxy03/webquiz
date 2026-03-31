package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizInstanceRepo extends JpaRepository<QuizInstance, Long> {

        // Tìm quiz instance theo quiz và user
        Optional<QuizInstance> findByQuizIdAndUserIdAndStatus(Long quizId, Long userId, QuizInstanceStatus status);

        // Tìm quiz instance theo quiz và guest
        Optional<QuizInstance> findByQuizIdAndGuestIdAndStatus(Long quizId, String guestId, QuizInstanceStatus status);

        // Latest in-progress instance (avoid NonUniqueResultException)
        Optional<QuizInstance> findTopByQuizIdAndUserIdAndStatusOrderByStartedAtDesc(Long quizId, Long userId,
                        QuizInstanceStatus status);

        Optional<QuizInstance> findTopByQuizIdAndGuestIdAndStatusOrderByStartedAtDesc(Long quizId, String guestId,
                        QuizInstanceStatus status);

        // Tìm quiz instance đang chạy của user
        List<QuizInstance> findByUserIdAndStatus(Long userId, QuizInstanceStatus status);

        // Tìm quiz instance theo quiz
        List<QuizInstance> findByQuizId(Long quizId);

        // Tìm quiz instance theo status
        List<QuizInstance> findByStatus(QuizInstanceStatus status);

        @Query(value = "SELECT qi.* FROM quiz_instances qi " +
                        "JOIN quizzes q ON qi.quiz_id = q.id " +
                        "WHERE qi.status = :status " +
                        "AND q.time_limit_minutes IS NOT NULL " +
                        "AND EXTRACT(EPOCH FROM (CAST(:now AS TIMESTAMP) - qi.started_at))/60 > q.time_limit_minutes", nativeQuery = true)
        List<QuizInstance> findTimedOutInstances(@Param("now") LocalDateTime now,
                        @Param("status") String status);

        // Kiểm tra xem user đã tham gia quiz chưa
        boolean existsByQuizIdAndUserId(Long quizId, Long userId);

        // Đếm số lần tham gia của user
        long countByQuizIdAndUserId(Long quizId, Long userId);

        // Tìm quiz instance theo quiz và status
        List<QuizInstance> findByQuizIdAndStatus(Long quizId, QuizInstanceStatus status);

        // Tìm quiz instance đang chạy
        List<QuizInstance> findByStatusAndStartedAtBefore(QuizInstanceStatus status, LocalDateTime before);
        // @Query(value = "SELECT * FROM quiz_instance qi " +
        // "WHERE qi.quiz_id = :quizId " +
        // "AND qi.lobby_id = :lobbyId",
        // nativeQuery = true)
        // Page<QuizInstance> findByQuizIdAndLobbyId(@Param("quizId") Long quizId,
        // @Param("lobbyId") Long lobbyId, Pageable pageable);

        // // Tìm số lượng quiz đã làm.
        long countByQuizIdAndUserIdAndStatusIn(Long quizId, Long userId, List<QuizInstanceStatus> statuses);

        // Pageable overloads for proper pagination (Design #6)
        Page<QuizInstance> findByUserIdAndStatus(Long userId, QuizInstanceStatus status, Pageable pageable);

        Page<QuizInstance> findByQuizIdAndStatus(Long quizId, QuizInstanceStatus status, Pageable pageable);

        Page<QuizInstance> findByQuizIdAndUserIdAndStatusIn(Long quizId, Long userId, List<QuizInstanceStatus> statuses,
                        Pageable pageable);

        Page<QuizInstance> findByQuiz_LobbyIdAndStatusIn(Long lobbyId, List<QuizInstanceStatus> statuses,
                        Pageable pageable);

        // Dashboard: count completed (SUBMITTED + TIMED_OUT)
        @Query("SELECT COUNT(qi) FROM QuizInstance qi WHERE qi.user.id = :userId AND qi.status IN :statuses")
        long countByUserIdAndStatusIn(@Param("userId") Long userId,
                        @Param("statuses") List<QuizInstanceStatus> statuses);

        long countByUserIdAndStatus(Long userId, QuizInstanceStatus status);

        List<QuizInstance> findAllByQuizIdAndUserIdAndStatus(Long quizId, Long userId, QuizInstanceStatus status);

        // Dashboard: in-progress instances with quiz details (fetch join — no N+1)
        @Query("SELECT qi FROM QuizInstance qi JOIN FETCH qi.quiz q WHERE qi.user.id = :userId AND qi.status = :status ORDER BY qi.startedAt DESC")
        List<QuizInstance> findInProgressWithQuizByUserId(@Param("userId") Long userId,
                        @Param("status") QuizInstanceStatus status);

        // Dashboard: recent completed instances with quiz details (fetch join)
        @Query("SELECT qi FROM QuizInstance qi JOIN FETCH qi.quiz q WHERE qi.user.id = :userId AND qi.status IN :statuses ORDER BY qi.updatedAt DESC")
        List<QuizInstance> findRecentByUserIdAndStatusIn(@Param("userId") Long userId,
                        @Param("statuses") List<QuizInstanceStatus> statuses, Pageable pageable);

        long countByQuizIdAndGuestIdAndStatusIn(Long quizId, String guestId, List<QuizInstanceStatus> of);

        @Modifying
        @Query("DELETE FROM QuizInstance i WHERE i.guestId IS NOT NULL AND (" +
                        "(i.status = 'IN_PROGRESS' AND i.updatedAt < :abandonedTime) OR " +
                        "(i.status IN ('SUBMITTED', 'COMPLETED') AND i.updatedAt < :completedTime))") // Thêm SUBMITTED
                                                                                                      // vào đây
        int deleteGuestInstancesByCriteria(
                        @Param("abandonedTime") LocalDateTime abandonedTime,
                        @Param("completedTime") LocalDateTime completedTime);

        @Query("""
                            SELECT qi FROM QuizInstance qi
                            JOIN FETCH qi.quiz q
                            JOIN FETCH q.lobby l
                            JOIN FETCH qi.user u
                            WHERE qi.id = :id
                        """)
        Optional<QuizInstance> findFullById(Long id);
}
