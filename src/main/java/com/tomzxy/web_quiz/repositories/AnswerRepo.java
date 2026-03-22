package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.enums.ContentType;
import com.tomzxy.web_quiz.models.Answer;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AnswerRepo extends JpaRepository<Answer, Long>, JpaSpecificationExecutor<Answer> {
    
    // Basic CRUD with pagination
    @NonNull
    Page<Answer> findAll(@NonNull Pageable pageable);

    @Query("SELECT a FROM Answer a WHERE a.isActive = :isActive")
    Page<Answer> findAllByActive(@Param("isActive") boolean isActive, Pageable pageable);
    
    // Basic queries
    Optional<Answer> findByAnswerName(String answerName);
    
    // Search functionality
    @Query("SELECT a FROM Answer a WHERE LOWER(a.answerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND a.isActive = true")
    Page<Answer> searchByAnswerName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Filter by answer type
    @Query("SELECT a FROM Answer a WHERE a.type = :answerType AND a.isActive = true")
    Page<Answer> findByAnswerType(@Param("answerType") ContentType answerType, Pageable pageable);
    
    // Filter by multiple answer types
    @Query("SELECT a FROM Answer a WHERE a.type IN :answerTypes AND a.isActive = true")
    Page<Answer> findByAnswerTypes(@Param("answerTypes") Set<ContentType> answerTypes, Pageable pageable);
    
    // Filter by correctness
    @Query("SELECT a FROM Answer a WHERE a.answerCorrect = :answerCorrect AND a.isActive = true")
    Page<Answer> findByAnswerCorrect(@Param("answerCorrect") boolean answerCorrect, Pageable pageable);

    // Find correct answers
    @Query("SELECT a FROM Answer a WHERE a.answerCorrect = true AND a.isActive = true")
    Page<Answer> findCorrectAnswers(Pageable pageable);

    // Find incorrect answers
    @Query("SELECT a FROM Answer a WHERE a.answerCorrect = false AND a.isActive = true")
    Page<Answer> findIncorrectAnswers(Pageable pageable);

    // Filter by question
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.isActive = true")
    Page<Answer> findByQuestionId(@Param("questionId") Long questionId, Pageable pageable);

    // Filter by multiple questions
    @Query("SELECT a FROM Answer a WHERE a.question.id IN :questionIds AND a.isActive = true")
    Page<Answer> findByQuestionIds(@Param("questionIds") Set<Long> questionIds, Pageable pageable);

    // Find answers by answer type and correctness
    @Query("SELECT a FROM Answer a WHERE a.type = :answerType AND a.answerCorrect = :answerCorrect AND a.isActive = true")
    Page<Answer> findByAnswerTypeAndAnswerCorrect(@Param("answerType") ContentType answerType,
                                             @Param("answerCorrect") boolean answerCorrect, Pageable pageable);

    // Find answers by question and correctness
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.answerCorrect = :answerCorrect AND a.isActive = true")
    Page<Answer> findByQuestionIdAndAnswerCorrect(@Param("questionId") Long questionId,
                                             @Param("answerCorrect") boolean answerCorrect, Pageable pageable);

    // Count answers by type
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.type = :answerType AND a.isActive = true")
    long countByAnswerType(@Param("answerType") ContentType answerType);

    // Count answers by correctness
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.answerCorrect = :answerCorrect AND a.isActive = true")
    long countByAnswerCorrect(@Param("answerCorrect") boolean answerCorrect);

    // Count answers by question
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.isActive = true")
    long countByQuestionId(@Param("questionId") Long questionId);

    // Count correct answers by question
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.answerCorrect = true AND a.isActive = true")
    long countCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
    
    // Find recent answers
    @Query("SELECT a FROM Answer a WHERE a.isActive = true ORDER BY a.createdAt DESC")
    Page<Answer> findRecentAnswers(Pageable pageable);
    
    // Check if answer exists by name and question
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Answer a " +
           "WHERE a.answerName = :answerName AND a.question.id = :questionId AND a.isActive = true")
    boolean existsByAnswerNameAndQuestionId(@Param("answerName") String answerName, 
                                           @Param("questionId") Long questionId);
    
    // Find all answer types used
    @Query("SELECT DISTINCT a.type FROM Answer a WHERE a.isActive = true")
    List<ContentType> findAllAnswerTypes();
    
    // Find answers by ID list
    @Query("SELECT a FROM Answer a WHERE a.id IN :ids AND a.isActive = true")
    List<Answer> findByIds(@Param("ids") List<Long> ids);
}
