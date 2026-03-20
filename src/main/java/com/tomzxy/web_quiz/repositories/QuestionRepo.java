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

    @Query("SELECT q FROM Question q WHERE q.isActive = :isActive")
    Page<Question> findAllByActive(@Param("isActive") boolean isActive, Pageable pageable);

    // Basic queries
    Optional<Question> findByQuestionName(String questionName);

    boolean existsByQuestionName(String questionName);

    // Search functionality
    @Query("SELECT q FROM Question q WHERE LOWER(q.questionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND q.isActive = true")
    Page<Question> searchByQuestionName(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Filter by level
    @Query("SELECT q FROM Question q WHERE q.level = :level AND q.isActive = true")
    Page<Question> findByLevel(@Param("level") Level level, Pageable pageable);

    // Filter by question type
    @Query("SELECT q FROM Question q WHERE q.questionType = :questionType AND q.isActive = true")
    Page<Question> findByQuestionType(@Param("questionType") QuestionAndAnswerType questionType, Pageable pageable);

    // Filter by subject - through QuizQuestionLink -> Quiz -> Subject
    @Query("SELECT DISTINCT q FROM Question q JOIN QuizQuestionLink qql ON qql.question = q JOIN qql.quiz quiz WHERE quiz.subject.id = :subjectId AND q.isActive = true")
    Page<Question> findBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable);

    // Filter by multiple levels
    @Query("SELECT q FROM Question q WHERE q.level IN :levels AND q.isActive = true")
    Page<Question> findByLevels(@Param("levels") Set<Level> levels, Pageable pageable);

    @Query("Select q.contentHash from Question q where q.contentHash IN :incomingHashes")
    List<String> findAllByContentHashIn(@Param("incomingHashes") List<String> incomingHashes);

    // Filter by multiple question types
    @Query("SELECT q FROM Question q WHERE q.questionType IN :questionTypes AND q.isActive = true")
    Page<Question> findByQuestionTypes(@Param("questionTypes") Set<QuestionAndAnswerType> questionTypes,
            Pageable pageable);

    // Filter by multiple subjects - through QuizQuestionLink -> Quiz -> Subject
    @Query("SELECT DISTINCT q FROM Question q JOIN QuizQuestionLink qql ON qql.question = q JOIN qql.quiz quiz WHERE quiz.subject.id IN :subjectIds AND q.isActive = true")
    Page<Question> findBySubjectIds(@Param("subjectIds") Set<Long> subjectIds, Pageable pageable);

    // Find questions with correct answers
    @Query("SELECT q FROM Question q JOIN q.answers a WHERE a.answerCorrect = true AND q.isActive = true")
    Page<Question> findQuestionsWithCorrectAnswers(Pageable pageable);

    // Find questions by level and type
    @Query("SELECT q FROM Question q WHERE q.level = :level AND q.questionType = :questionType AND q.isActive = true")
    Page<Question> findByLevelAndType(@Param("level") Level level,
            @Param("questionType") QuestionAndAnswerType questionType, Pageable pageable);

    // Find questions by subject and level - through QuizQuestionLink -> Quiz ->
    // Subject
    @Query("SELECT DISTINCT q FROM Question q JOIN QuizQuestionLink qql ON qql.question = q JOIN qql.quiz quiz WHERE quiz.subject.id = :subjectId AND q.level = :level AND q.isActive = true")
    Page<Question> findBySubjectIdAndLevel(@Param("subjectId") Long subjectId,
            @Param("level") Level level, Pageable pageable);

    // Find questions by subject and type - through QuizQuestionLink -> Quiz ->
    // Subject
    @Query("SELECT DISTINCT q FROM Question q JOIN QuizQuestionLink qql ON qql.question = q JOIN qql.quiz quiz WHERE quiz.subject.id = :subjectId AND q.questionType = :questionType AND q.isActive = true")
    Page<Question> findBySubjectIdAndType(@Param("subjectId") Long subjectId,
            @Param("questionType") QuestionAndAnswerType questionType, Pageable pageable);

    // Count questions by level
    @Query("SELECT COUNT(q) FROM Question q WHERE q.level = :level AND q.isActive = true")
    long countByLevel(@Param("level") Level level);

    // Count questions by subject - through QuizQuestionLink -> Quiz -> Subject
    @Query("SELECT COUNT(DISTINCT q) FROM Question q JOIN QuizQuestionLink qql ON qql.question = q JOIN qql.quiz quiz WHERE quiz.subject.id = :subjectId AND q.isActive = true")
    long countBySubjectId(@Param("subjectId") Long subjectId);

    // Count questions by type
    @Query("SELECT COUNT(q) FROM Question q WHERE q.questionType = :questionType AND q.isActive = true")
    long countByQuestionType(@Param("questionType") QuestionAndAnswerType questionType);

    // Find recent questions
    @Query("SELECT q FROM Question q WHERE q.isActive = true ORDER BY q.createdAt DESC")
    Page<Question> findRecentQuestions(Pageable pageable);

    // Find questions without answers
    @Query("SELECT q FROM Question q WHERE SIZE(q.answers) = 0 AND q.isActive = true")
    Page<Question> findQuestionsWithoutAnswers(Pageable pageable);

    // Find questions used in quizzes
    @Query("SELECT DISTINCT q FROM Question q JOIN QuizQuestionLink qql ON qql.question = q WHERE q.isActive = true")
    Page<Question> findQuestionsUsedInQuizzes(Pageable pageable);

    // Check if question exists by name and subject - through QuizQuestionLink ->
    // Quiz -> Subject
    @Query("SELECT CASE WHEN COUNT(DISTINCT q) > 0 THEN true ELSE false END FROM Question q " +
            "JOIN QuizQuestionLink qql ON qql.question = q JOIN qql.quiz quiz " +
            "WHERE q.questionName = :questionName AND quiz.subject.id = :subjectId AND q.isActive = true")
    boolean existsByQuestionNameAndSubjectId(@Param("questionName") String questionName,
            @Param("subjectId") Long subjectId);

    // Find all levels used by questions
    @Query("SELECT DISTINCT q.level FROM Question q WHERE q.isActive = true")
    List<Level> findAllLevels();

    // Find all question names
    @Query("SELECT q.questionName FROM Question q WHERE q.questionName IN :names AND q.isActive = true")
    List<String> findAllQuestionNames(@Param("names") List<String> names);

    // Find all question types used
    @Query("SELECT DISTINCT q.questionType FROM Question q WHERE q.isActive = true")
    List<QuestionAndAnswerType> findAllQuestionTypes();

    // Find questions by ID list
    @Query("SELECT q FROM Question q WHERE q.id IN :ids AND q.isActive = true")
    List<Question> findByIds(@Param("ids") List<Long> ids);
    
    // Find questions by bank ID
    @Query("SELECT q FROM Question q WHERE q.bank.id = :bankId AND q.isActive = true")
    Page<Question> findByBankId(@Param("bankId") Long bankId, Pageable pageable);
    
    // Find questions by bank ID without pagination
    @Query("SELECT q FROM Question q WHERE q.bank.id = :bankId AND q.isActive = true")
    List<Question> findByBankId(@Param("bankId") Long bankId);
    
    // OPTIMIZATION: Find questions with eager-loaded answers to avoid N+1
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN FETCH q.answers " +
           "WHERE q.bank.id = :bankId AND q.isActive = true")
    List<Question> findByBankIdWithAnswers(@Param("bankId") Long bankId);
    
    // Check question ownership in single query
    @Query("SELECT COUNT(q) > 0 FROM Question q " +
           "WHERE q.id = :questionId AND q.bank.owner.id = :userId")
    boolean existsByIdAndOwnerId(@Param("questionId") Long questionId, 
                                 @Param("userId") Long userId);

    List<Question> findByFolderId(Long folderId);
}
