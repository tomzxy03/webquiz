package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Host.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionBankRepo extends JpaRepository<QuestionBank, Long> {
    
    // Find question bank by owner ID
    Optional<QuestionBank> findByOwnerId(Long ownerId);
    
    // Check if question bank exists for owner
    boolean existsByOwnerId(Long ownerId);
    
    // Get all active question banks with pagination
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.isActive = true")
    Page<QuestionBank> findAllActive(Pageable pageable);
    
    // Get question bank with questions and folders count
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.isActive = true")
    Page<QuestionBank> findAll(Pageable pageable);
    
    /**
     * Check if folder belongs to owner's bank (via folder's bank_id)
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
           "FROM Folder f WHERE f.id = :folderId AND f.bank.owner.id = :ownerId")
    boolean existsByIdAndOwnerId(@Param("folderId") Long folderId, @Param("ownerId") Long ownerId);
    
    /**
     * Check if question belongs to owner's bank
     */
    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END " +
           "FROM Question q WHERE q.id = :questionId AND q.bank.owner.id = :ownerId")
    boolean existsByQuestionIdAndOwnerId(@Param("questionId") Long questionId, @Param("ownerId") Long ownerId);
}
