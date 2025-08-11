package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Quiz;
import com.tomzxy.web_quiz.enums.QuizType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {
    
    // Basic CRUD with pagination
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true")
    Page<Quiz> findAllActive(Pageable pageable);
    
    // Find by host
    @Query("SELECT q FROM Quiz q WHERE q.host.id = :hostId AND q.isActive = true")
    Page<Quiz> findByHostId(@Param("hostId") Long hostId, Pageable pageable);
    
    // Find by group
    @Query("SELECT q FROM Quiz q WHERE q.lobby.id = :lobbyId AND q.isActive = true")
    Page<Quiz> findByLobbyId(@Param("lobbyId") Long lobbyId, Pageable pageable);
    
    // Find by quiz type
    @Query("SELECT q FROM Quiz q WHERE q.quizType = :quizType AND q.isActive = true")
    Page<Quiz> findByQuizType(@Param("quizType") QuizType quizType, Pageable pageable);
    
    // Search by title or description
    @Query("SELECT q FROM Quiz q WHERE (LOWER(q.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(q.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND q.isActive = true")
    Page<Quiz> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by host and group
    @Query("SELECT q FROM Quiz q WHERE q.host.id = :hostId AND q.lobby.id = :lobbyId AND q.isActive = true")
    Page<Quiz> findByHostIdAndLobbyId(@Param("hostId") Long hostId, @Param("lobbyId") Long lobbyId, Pageable pageable);
    
    // Find quizzes with results count
    @Query("SELECT q FROM Quiz q WHERE SIZE(q.results) > 0 AND q.isActive = true")
    Page<Quiz> findQuizzesWithResults(Pageable pageable);
    
    // Find quizzes without results
    @Query("SELECT q FROM Quiz q WHERE SIZE(q.results) = 0 AND q.isActive = true")
    Page<Quiz> findQuizzesWithoutResults(Pageable pageable);
    
    // Find by subject
    @Query("SELECT q FROM Quiz q WHERE q.subject.id = :subjectId AND q.isActive = true")
    Page<Quiz> findBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable);
    
    // Count quizzes by host
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.host.id = :hostId AND q.isActive = true")
    long countByHostId(@Param("hostId") Long hostId);
    
    // Count quizzes by group
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.lobby.id = :lobbyId AND q.isActive = true")
    long countByLobbyId(@Param("lobbyId") Long lobbyId);
    
    // Find recent quizzes
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true ORDER BY q.createdAt DESC")
    Page<Quiz> findRecentQuizzes(Pageable pageable);
    
    // Find popular quizzes (with most results)
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true ORDER BY SIZE(q.results) DESC")
    Page<Quiz> findPopularQuizzes(Pageable pageable);
    
    // Check if quiz exists by title and host
    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM Quiz q " +
           "WHERE q.title = :title AND q.host.id = :hostId AND q.isActive = true")
    boolean existsByTitleAndHostId(@Param("title") String title, @Param("hostId") Long hostId);
    
    // Find all quiz types used by a host
    @Query("SELECT DISTINCT q.quizType FROM Quiz q WHERE q.host.id = :hostId AND q.isActive = true")
    List<QuizType> findQuizTypesByHostId(@Param("hostId") Long hostId);
    
    // Find quizzes with questions count
    @Query("SELECT q FROM Quiz q WHERE SIZE(q.questions) = :questionCount AND q.isActive = true")
    Page<Quiz> findByQuestionsCount(@Param("questionCount") int questionCount, Pageable pageable);
} 