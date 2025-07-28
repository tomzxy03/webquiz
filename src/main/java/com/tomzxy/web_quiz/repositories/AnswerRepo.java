package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
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

    @Query("SELECT a FROM Answer a WHERE a.is_active = :is_active")
    Page<Answer> findAllByActive(@Param("is_active") boolean is_active, Pageable pageable);
    
    // Basic queries
    Optional<Answer> findByAnswerName(String answerName);
    
    // Search functionality
    @Query("SELECT a FROM Answer a WHERE LOWER(a.answerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND a.is_active = true")
    Page<Answer> searchByAnswerName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Filter by answer type
    @Query("SELECT a FROM Answer a WHERE a.answerType = :answerType AND a.is_active = true")
    Page<Answer> findByAnswerType(@Param("answerType") QuestionAndAnswerType answerType, Pageable pageable);
    
    // Filter by multiple answer types
    @Query("SELECT a FROM Answer a WHERE a.answerType IN :answerTypes AND a.is_active = true")
    Page<Answer> findByAnswerTypes(@Param("answerTypes") Set<QuestionAndAnswerType> answerTypes, Pageable pageable);
    
    // Filter by correctness
    @Query("SELECT a FROM Answer a WHERE a.isCorrect = :isCorrect AND a.is_active = true")
    Page<Answer> findByIsCorrect(@Param("isCorrect") boolean isCorrect, Pageable pageable);
    
    // Find correct answers
    @Query("SELECT a FROM Answer a WHERE a.isCorrect = true AND a.is_active = true")
    Page<Answer> findCorrectAnswers(Pageable pageable);
    
    // Find incorrect answers
    @Query("SELECT a FROM Answer a WHERE a.isCorrect = false AND a.is_active = true")
    Page<Answer> findIncorrectAnswers(Pageable pageable);
    
    // Filter by question
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.is_active = true")
    Page<Answer> findByQuestionId(@Param("questionId") Long questionId, Pageable pageable);
    
    // Filter by multiple questions
    @Query("SELECT a FROM Answer a WHERE a.question.id IN :questionIds AND a.is_active = true")
    Page<Answer> findByQuestionIds(@Param("questionIds") Set<Long> questionIds, Pageable pageable);
    
    // Find answers by question name
    @Query("SELECT a FROM Answer a JOIN a.question q WHERE q.questionName = :questionName AND a.is_active = true")
    Page<Answer> findByQuestionName(@Param("questionName") String questionName, Pageable pageable);
    
    // Find answers by question level
    @Query("SELECT a FROM Answer a JOIN a.question q WHERE q.level = :level AND a.is_active = true")
    Page<Answer> findByQuestionLevel(@Param("level") String level, Pageable pageable);
    
    // Find answers by question type
    @Query("SELECT a FROM Answer a JOIN a.question q WHERE q.questionType = :questionType AND a.is_active = true")
    Page<Answer> findByQuestionType(@Param("questionType") QuestionAndAnswerType questionType, Pageable pageable);
    
    // Find answers by subject
    @Query("SELECT a FROM Answer a JOIN a.question q JOIN q.subject s WHERE s.id = :subjectId AND a.is_active = true")
    Page<Answer> findBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable);
    
    // Find answers by subject name
    @Query("SELECT a FROM Answer a JOIN a.question q JOIN q.subject s WHERE s.subjectName = :subjectName AND a.is_active = true")
    Page<Answer> findBySubjectName(@Param("subjectName") String subjectName, Pageable pageable);
    
    // Find answers by answer type and correctness
    @Query("SELECT a FROM Answer a WHERE a.answerType = :answerType AND a.isCorrect = :isCorrect AND a.is_active = true")
    Page<Answer> findByAnswerTypeAndIsCorrect(@Param("answerType") QuestionAndAnswerType answerType, 
                                             @Param("isCorrect") boolean isCorrect, Pageable pageable);
    
    // Find answers by question and correctness
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = :isCorrect AND a.is_active = true")
    Page<Answer> findByQuestionIdAndIsCorrect(@Param("questionId") Long questionId, 
                                             @Param("isCorrect") boolean isCorrect, Pageable pageable);
    
    // Count answers by type
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.answerType = :answerType AND a.is_active = true")
    long countByAnswerType(@Param("answerType") QuestionAndAnswerType answerType);
    
    // Count answers by correctness
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.isCorrect = :isCorrect AND a.is_active = true")
    long countByIsCorrect(@Param("isCorrect") boolean isCorrect);
    
    // Count answers by question
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.is_active = true")
    long countByQuestionId(@Param("questionId") Long questionId);
    
    // Count correct answers by question
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true AND a.is_active = true")
    long countCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
    
    // Find recent answers
    @Query("SELECT a FROM Answer a WHERE a.is_active = true ORDER BY a.create_at DESC")
    Page<Answer> findRecentAnswers(Pageable pageable);
    
    // Find answers used in quiz questions
    @Query("SELECT DISTINCT a FROM Answer a JOIN QuizAnswer qa ON a.id = qa.answer.id WHERE a.is_active = true")
    Page<Answer> findAnswersUsedInQuizzes(Pageable pageable);
    
    // Find answers not used in any quiz
    @Query("SELECT a FROM Answer a WHERE a.id NOT IN " +
           "(SELECT DISTINCT qa.answer.id FROM QuizAnswer qa WHERE qa.answer IS NOT NULL) AND a.is_active = true")
    Page<Answer> findAnswersNotUsedInQuizzes(Pageable pageable);
    
    // Check if answer exists by name and question
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Answer a " +
           "WHERE a.answerName = :answerName AND a.question.id = :questionId AND a.is_active = true")
    boolean existsByAnswerNameAndQuestionId(@Param("answerName") String answerName, 
                                           @Param("questionId") Long questionId);
    
    // Find all answer types used
    @Query("SELECT DISTINCT a.answerType FROM Answer a WHERE a.is_active = true")
    List<QuestionAndAnswerType> findAllAnswerTypes();
    
    // Find answers by ID list
    @Query("SELECT a FROM Answer a WHERE a.id IN :ids AND a.is_active = true")
    List<Answer> findByIds(@Param("ids") List<Long> ids);
    
    // Find answers by name pattern and type
    @Query("SELECT a FROM Answer a WHERE LOWER(a.answerName) LIKE LOWER(CONCAT('%', :namePattern, '%')) " +
           "AND a.answerType = :answerType AND a.is_active = true")
    Page<Answer> findByAnswerNamePatternAndType(@Param("namePattern") String namePattern, 
                                               @Param("answerType") QuestionAndAnswerType answerType, Pageable pageable);
    
    // Find answers by question and type
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.answerType = :answerType AND a.is_active = true")
    Page<Answer> findByQuestionIdAndAnswerType(@Param("questionId") Long questionId, 
                                              @Param("answerType") QuestionAndAnswerType answerType, Pageable pageable);
    
    // Find answers with specific name pattern
    @Query("SELECT a FROM Answer a WHERE a.answerName LIKE :namePattern AND a.is_active = true")
    Page<Answer> findByAnswerNamePattern(@Param("namePattern") String namePattern, Pageable pageable);
}
