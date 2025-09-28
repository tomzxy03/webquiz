package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepo extends JpaRepository<Subject, Long>, JpaSpecificationExecutor<Subject> {
    
    // Basic CRUD with pagination
    @Query("SELECT s FROM Subject s WHERE s.isActive = :isActive")
    Page<Subject> findAllActive(@Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT s FROM Subject s WHERE s.isActive = :isActive")
    Page<Subject> findAllByActive(@Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT s FROM Subject s WHERE s.isActive = :isActive")
    Optional<List<Subject>> findAllByActive(@Param("isActive") boolean isActive);
    // Find by subject name
    @Query("SELECT s FROM Subject s WHERE s.subjectName = :subjectName AND s.isActive = :isActive")
    Optional<Subject> findBySubjectName(@Param("subjectName") String subjectName);
    
    // Check if subject exists by name
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subject s " +
           "WHERE s.subjectName = :subjectName AND s.isActive = :isActive")
    boolean existsBySubjectName(@Param("subjectName") String subjectName);
    
    
    // Find subjects by quizzes count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.quizzes) = :quizCount AND s.isActive = :isActive")
    Page<Subject> findByQuizCount(@Param("quizCount") int quizCount, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Find subjects with minimum quizzes count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.quizzes) >= :minQuizCount AND s.isActive = :isActive")
    Page<Subject> findByMinQuizCount(@Param("minQuizCount") int minQuizCount, @Param("isActive") boolean isActive, Pageable pageable);
    
   
    
    // Find subjects with most quizzes
    @Query("SELECT s FROM Subject s WHERE s.isActive = :isActive ORDER BY SIZE(s.quizzes) DESC")
    Page<Subject> findSubjectsWithMostQuizzes(@Param("isActive") boolean isActive, Pageable pageable);
    
    // Find recent subjects
    @Query("SELECT s FROM Subject s WHERE s.isActive = :isActive ORDER BY s.createdAt DESC")
    Page<Subject> findRecentSubjects(@Param("isActive") boolean isActive, Pageable pageable);
    
    // Count subjects by quiz count
    @Query("SELECT COUNT(s) FROM Subject s WHERE SIZE(s.quizzes) = :quizCount AND s.isActive = :isActive")
    long countByQuizCount(@Param("quizCount") int quizCount, @Param("isActive") boolean isActive);
    
    // Find all subject names
    @Query("SELECT s.subjectName FROM Subject s WHERE s.isActive = :isActive")
    List<String> findAllSubjectNames(@Param("isActive") boolean isActive);
    
    // Find subjects by ID list
    @Query("SELECT s FROM Subject s WHERE s.id IN :ids AND s.isActive = :isActive")
    List<Subject> findByIds(@Param("ids") List<Long> ids, @Param("isActive") boolean isActive);
    
    // Find subjects with description containing specific text
    @Query("SELECT s FROM Subject s WHERE LOWER(s.description) LIKE LOWER(CONCAT('%', :descriptionText, '%')) AND s.isActive = :isActive")
    Page<Subject> findByDescriptionContaining(@Param("descriptionText") String descriptionText, @Param("isActive") boolean isActive, Pageable pageable);
    
}
