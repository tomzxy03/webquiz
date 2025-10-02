package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.models.Quiz.QuizInstance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizInstanceRepo extends JpaRepository<QuizInstance, Long> {
    
    // Tìm quiz instance theo quiz và user
    Optional<QuizInstance> findByQuizIdAndUserIdAndStatus(Long quizId, Long userId, QuizInstanceStatus status);
    
    // Tìm quiz instance đang chạy của user
    List<QuizInstance> findByUserIdAndStatus(Long userId, QuizInstanceStatus status);
    
    // Tìm quiz instance theo quiz
    List<QuizInstance> findByQuizId(Long quizId);
    
    // Tìm quiz instance theo status
    List<QuizInstance> findByStatus(QuizInstanceStatus status);
    
    @Query(value = "SELECT * FROM quiz_instance qi " +
               "WHERE qi.status = :status " +
               "AND qi.time_limit_minutes IS NOT NULL " +
               "AND TIMESTAMPDIFF(MINUTE, qi.started_at, :now) > qi.time_limit_minutes",
       nativeQuery = true)
List<QuizInstance> findTimedOutInstances(@Param("now") LocalDateTime now,
                                         @Param("status") QuizInstanceStatus status);
    
    // Kiểm tra xem user đã tham gia quiz chưa
    boolean existsByQuizIdAndUserId(Long quizId, Long userId);
    
    // Đếm số lần tham gia của user
    long countByQuizIdAndUserId(Long quizId, Long userId);
    
    // Tìm quiz instance theo quiz và status
    List<QuizInstance> findByQuizIdAndStatus(Long quizId, QuizInstanceStatus status);
    
    // Tìm quiz instance đang chạy
    List<QuizInstance> findByStatusAndStartedAtBefore(QuizInstanceStatus status, LocalDateTime before);

//    @Query(value = "SELECT * FROM quiz_instance qi " +
//               "WHERE qi.quiz_id = :quizId " +
//               "AND qi.lobby_id = :lobbyId",
//       nativeQuery = true)
//    Page<QuizInstance> findByQuizIdAndLobbyId(@Param("quizId") Long quizId, @Param("lobbyId") Long lobbyId, Pageable pageable);


//    // Tìm số lượng quiz đã làm.
//    int countByQuizzesAndUserId(Long userId);

} 