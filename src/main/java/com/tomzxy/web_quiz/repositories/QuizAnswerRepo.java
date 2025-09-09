package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnswerRepo extends JpaRepository<QuizAnswer, Long> {
    
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.quizQuestion.id = :quizQuestionId")
    List<QuizAnswer> findByQuizQuestionId(@Param("quizQuestionId") Long quizQuestionId);
    
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.answer.id = :answerId")
    List<QuizAnswer> findByAnswerId(@Param("answerId") Long answerId);
    
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.isCustom = :isCustom")
    List<QuizAnswer> findByIsCustom(@Param("isCustom") Boolean isCustom);
    
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.isCorrect = :isCorrect")
    List<QuizAnswer> findByIsCorrect(@Param("isCorrect") Boolean isCorrect);
    
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.quizQuestion.id = :quizQuestionId AND qa.isCorrect = :isCorrect")
    List<QuizAnswer> findByQuizQuestionIdAndIsCorrect(@Param("quizQuestionId") Long quizQuestionId, @Param("isCorrect") Boolean isCorrect);
}
