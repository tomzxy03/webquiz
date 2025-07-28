package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.models.Question;
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
public interface QuestionRepo extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    
    // Basic CRUD with pagination
    @NonNull
    Page<Question> findAll(@NonNull Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.is_active = :is_active")
    Page<Question> findAllByActive(@Param("is_active") boolean is_active, Pageable pageable);

    // Basic queries
    Optional<Question> findByQuestionName(String questionName);
    Optional<Question> findByLevel(Level level);
    boolean existsByQuestionName(String questionName);
    
    // Search functionality
    @Query("SELECT q FROM Question q WHERE LOWER(q.questionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND q.is_active = true")
    Page<Question> searchByQuestionName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Filter by level
    @Query("SELECT q FROM Question q WHERE q.level = :level AND q.is_active = true")
    Page<Question> findByLevel(@Param("level") Level level, Pageable pageable);
    
    // Filter by question type
    @Query("SELECT q FROM Question q WHERE q.questionType = :questionType AND q.is_active = true")
    Page<Question> findByQuestionType(@Param("questionType") QuestionAndAnswerType questionType, Pageable pageable);
    
    // Filter by subject
    @Query("SELECT q FROM Question q WHERE q.subject.id = :subjectId AND q.is_active = true")
    Page<Question> findBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable);
    
    // Filter by subject name
    @Query("SELECT q FROM Question q WHERE q.subject.subjectName = :subjectName AND q.is_active = true")
    Page<Question> findBySubjectName(@Param("subjectName") String subjectName, Pageable pageable);
    
    // Filter by multiple levels
    @Query("SELECT q FROM Question q WHERE q.level IN :levels AND q.is_active = true")
    Page<Question> findByLevels(@Param("levels") Set<Level> levels, Pageable pageable);
    
    // Filter by multiple question types
    @Query("SELECT q FROM Question q WHERE q.questionType IN :questionTypes AND q.is_active = true")
    Page<Question> findByQuestionTypes(@Param("questionTypes") Set<QuestionAndAnswerType> questionTypes, Pageable pageable);
    
    // Filter by multiple subjects
    @Query("SELECT q FROM Question q WHERE q.subject.id IN :subjectIds AND q.is_active = true")
    Page<Question> findBySubjectIds(@Param("subjectIds") Set<Long> subjectIds, Pageable pageable);
    
    // Find questions with answers count
    @Query("SELECT q FROM Question q WHERE SIZE(q.answers) = :answerCount AND q.is_active = true")
    Page<Question> findByAnswersCount(@Param("answerCount") int answerCount, Pageable pageable);
    
    // Find questions with minimum answers count
    @Query("SELECT q FROM Question q WHERE SIZE(q.answers) >= :minAnswerCount AND q.is_active = true")
    Page<Question> findByMinAnswersCount(@Param("minAnswerCount") int minAnswerCount, Pageable pageable);
    
    // Find questions with correct answers
    @Query("SELECT q FROM Question q JOIN q.answers a WHERE a.isCorrect = true AND q.is_active = true")
    Page<Question> findQuestionsWithCorrectAnswers(Pageable pageable);
    
    // Find questions by level and type
    @Query("SELECT q FROM Question q WHERE q.level = :level AND q.questionType = :questionType AND q.is_active = true")
    Page<Question> findByLevelAndType(@Param("level") Level level, 
                                     @Param("questionType") QuestionAndAnswerType questionType, Pageable pageable);
    
    // Find questions by subject and level
    @Query("SELECT q FROM Question q WHERE q.subject.id = :subjectId AND q.level = :level AND q.is_active = true")
    Page<Question> findBySubjectIdAndLevel(@Param("subjectId") Long subjectId, 
                                          @Param("level") Level level, Pageable pageable);
    
    // Find questions by subject and type
    @Query("SELECT q FROM Question q WHERE q.subject.id = :subjectId AND q.questionType = :questionType AND q.is_active = true")
    Page<Question> findBySubjectIdAndType(@Param("subjectId") Long subjectId, 
                                         @Param("questionType") QuestionAndAnswerType questionType, Pageable pageable);
    
    // Count questions by level
    @Query("SELECT COUNT(q) FROM Question q WHERE q.level = :level AND q.is_active = true")
    long countByLevel(@Param("level") Level level);
    
    // Count questions by subject
    @Query("SELECT COUNT(q) FROM Question q WHERE q.subject.id = :subjectId AND q.is_active = true")
    long countBySubjectId(@Param("subjectId") Long subjectId);
    
    // Count questions by type
    @Query("SELECT COUNT(q) FROM Question q WHERE q.questionType = :questionType AND q.is_active = true")
    long countByQuestionType(@Param("questionType") QuestionAndAnswerType questionType);
    
    // Find recent questions
    @Query("SELECT q FROM Question q WHERE q.is_active = true ORDER BY q.create_at DESC")
    Page<Question> findRecentQuestions(Pageable pageable);
    
    // Find questions with most answers
    @Query("SELECT q FROM Question q WHERE q.is_active = true ORDER BY SIZE(q.answers) DESC")
    Page<Question> findQuestionsWithMostAnswers(Pageable pageable);
    
    // Find questions without answers
    @Query("SELECT q FROM Question q WHERE SIZE(q.answers) = 0 AND q.is_active = true")
    Page<Question> findQuestionsWithoutAnswers(Pageable pageable);
    
    // Find questions used in quizzes
    @Query("SELECT DISTINCT q FROM Question q JOIN QuizQuestion qq ON q.id = qq.question.id WHERE q.is_active = true")
    Page<Question> findQuestionsUsedInQuizzes(Pageable pageable);
    
    // Find questions not used in any quiz
    @Query("SELECT q FROM Question q WHERE q.id NOT IN " +
           "(SELECT DISTINCT qq.question.id FROM QuizQuestion qq WHERE qq.question IS NOT NULL) AND q.is_active = true")
    Page<Question> findQuestionsNotUsedInQuizzes(Pageable pageable);
    
    // Check if question exists by name and subject
    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM Question q " +
           "WHERE q.questionName = :questionName AND q.subject.id = :subjectId AND q.is_active = true")
    boolean existsByQuestionNameAndSubjectId(@Param("questionName") String questionName, 
                                            @Param("subjectId") Long subjectId);
    
    // Find all levels used by questions
    @Query("SELECT DISTINCT q.level FROM Question q WHERE q.is_active = true")
    List<Level> findAllLevels();
    
    // Find all question types used
    @Query("SELECT DISTINCT q.questionType FROM Question q WHERE q.is_active = true")
    List<QuestionAndAnswerType> findAllQuestionTypes();
    
    // Find questions by answer name (questions that have answers with specific name)
    @Query("SELECT q FROM Question q JOIN q.answers a WHERE a.answerName LIKE CONCAT('%', :answerName, '%') AND q.is_active = true")
    Page<Question> findByAnswerName(@Param("answerName") String answerName, Pageable pageable);
    
    // Find questions with specific answer type
    @Query("SELECT q FROM Question q JOIN q.answers a WHERE a.answerType = :answerType AND q.is_active = true")
    Page<Question> findByAnswerType(@Param("answerType") QuestionAndAnswerType answerType, Pageable pageable);
    
    // Bulk operations
    @Query("SELECT q.questionName FROM Question q WHERE q.questionName IN :names")
    List<String> findAllExistQuestion(@Param("names") List<String> names);
    
    // Find questions by ID list
    @Query("SELECT q FROM Question q WHERE q.id IN :ids AND q.is_active = true")
    List<Question> findByIds(@Param("ids") List<Long> ids);
}
