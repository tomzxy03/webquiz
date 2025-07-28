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
    @Query("SELECT s FROM Subject s WHERE s.is_active = true")
    Page<Subject> findAllActive(Pageable pageable);

    @Query("SELECT s FROM Subject s WHERE s.is_active = :is_active")
    Page<Subject> findAllByActive(@Param("is_active") boolean is_active, Pageable pageable);

    @Query("SELECT s FROM Subject s WHERE s.is_active = :is_active")
    Optional<List<Subject>> findAllByActive(@Param("is_active") boolean is_active);
    
    // Search functionality
    @Query("SELECT s FROM Subject s WHERE LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND s.is_active = true")
    Page<Subject> searchBySubjectNameOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by subject name
    @Query("SELECT s FROM Subject s WHERE s.subjectName = :subjectName AND s.is_active = true")
    Optional<Subject> findBySubjectName(@Param("subjectName") String subjectName);
    
    // Check if subject exists by name
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subject s " +
           "WHERE s.subjectName = :subjectName AND s.is_active = true")
    boolean existsBySubjectName(@Param("subjectName") String subjectName);
    
    // Find subjects by questions count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.questions) = :questionCount AND s.is_active = true")
    Page<Subject> findByQuestionCount(@Param("questionCount") int questionCount, Pageable pageable);
    
    // Find subjects with minimum questions count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.questions) >= :minQuestionCount AND s.is_active = true")
    Page<Subject> findByMinQuestionCount(@Param("minQuestionCount") int minQuestionCount, Pageable pageable);
    
    // Find subjects with maximum questions count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.questions) <= :maxQuestionCount AND s.is_active = true")
    Page<Subject> findByMaxQuestionCount(@Param("maxQuestionCount") int maxQuestionCount, Pageable pageable);
    
    // Find subjects by question count range
    @Query("SELECT s FROM Subject s WHERE SIZE(s.questions) BETWEEN :minQuestionCount AND :maxQuestionCount AND s.is_active = true")
    Page<Subject> findByQuestionCountRange(@Param("minQuestionCount") int minQuestionCount, 
                                         @Param("maxQuestionCount") int maxQuestionCount, Pageable pageable);
    
    // Find subjects by quizzes count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.quizzes) = :quizCount AND s.is_active = true")
    Page<Subject> findByQuizCount(@Param("quizCount") int quizCount, Pageable pageable);
    
    // Find subjects with minimum quizzes count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.quizzes) >= :minQuizCount AND s.is_active = true")
    Page<Subject> findByMinQuizCount(@Param("minQuizCount") int minQuizCount, Pageable pageable);
    
    // Find subjects without questions
    @Query("SELECT s FROM Subject s WHERE SIZE(s.questions) = 0 AND s.is_active = true")
    Page<Subject> findSubjectsWithoutQuestions(Pageable pageable);
    
    // Find subjects without quizzes
    @Query("SELECT s FROM Subject s WHERE SIZE(s.quizzes) = 0 AND s.is_active = true")
    Page<Subject> findSubjectsWithoutQuizzes(Pageable pageable);
    
    // Find subjects with most questions
    @Query("SELECT s FROM Subject s WHERE s.is_active = true ORDER BY SIZE(s.questions) DESC")
    Page<Subject> findSubjectsWithMostQuestions(Pageable pageable);
    
    // Find subjects with most quizzes
    @Query("SELECT s FROM Subject s WHERE s.is_active = true ORDER BY SIZE(s.quizzes) DESC")
    Page<Subject> findSubjectsWithMostQuizzes(Pageable pageable);
    
    // Find recent subjects
    @Query("SELECT s FROM Subject s WHERE s.is_active = true ORDER BY s.create_at DESC")
    Page<Subject> findRecentSubjects(Pageable pageable);
    
    // Count subjects by question count
    @Query("SELECT COUNT(s) FROM Subject s WHERE SIZE(s.questions) = :questionCount AND s.is_active = true")
    long countByQuestionCount(@Param("questionCount") int questionCount);
    
    // Count subjects by quiz count
    @Query("SELECT COUNT(s) FROM Subject s WHERE SIZE(s.quizzes) = :quizCount AND s.is_active = true")
    long countByQuizCount(@Param("quizCount") int quizCount);
    
    // Find subjects by question count and quiz count
    @Query("SELECT s FROM Subject s WHERE SIZE(s.questions) = :questionCount AND SIZE(s.quizzes) = :quizCount AND s.is_active = true")
    Page<Subject> findByQuestionCountAndQuizCount(@Param("questionCount") int questionCount, 
                                                 @Param("quizCount") int quizCount, Pageable pageable);
    
    // Find all subject names
    @Query("SELECT s.subjectName FROM Subject s WHERE s.is_active = true")
    List<String> findAllSubjectNames();
    
    // Find subjects by ID list
    @Query("SELECT s FROM Subject s WHERE s.id IN :ids AND s.is_active = true")
    List<Subject> findByIds(@Param("ids") List<Long> ids);
    
    // Find subjects with specific question count and name pattern
    @Query("SELECT s FROM Subject s WHERE SIZE(s.questions) = :questionCount " +
           "AND LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :namePattern, '%')) AND s.is_active = true")
    Page<Subject> findByQuestionCountAndNamePattern(@Param("questionCount") int questionCount, 
                                                   @Param("namePattern") String namePattern, Pageable pageable);
    
    // Find subjects with description containing specific text
    @Query("SELECT s FROM Subject s WHERE LOWER(s.description) LIKE LOWER(CONCAT('%', :descriptionText, '%')) AND s.is_active = true")
    Page<Subject> findByDescriptionContaining(@Param("descriptionText") String descriptionText, Pageable pageable);
    
    // Find subjects with empty description
    @Query("SELECT s FROM Subject s WHERE (s.description IS NULL OR s.description = '') AND s.is_active = true")
    Page<Subject> findSubjectsWithEmptyDescription(Pageable pageable);
    
    // Find subjects with non-empty description
    @Query("SELECT s FROM Subject s WHERE s.description IS NOT NULL AND s.description != '' AND s.is_active = true")
    Page<Subject> findSubjectsWithDescription(Pageable pageable);
}
