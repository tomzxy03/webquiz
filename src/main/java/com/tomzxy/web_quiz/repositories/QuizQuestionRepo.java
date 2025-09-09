package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizQuestionRepo extends JpaRepository<QuizQuestion, Long> {
    
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.quiz.id = :quizId")
    List<QuizQuestion> findByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.question.id = :questionId")
    List<QuizQuestion> findByQuestionId(@Param("questionId") Long questionId);
    
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.isCustom = :isCustom")
    List<QuizQuestion> findByIsCustom(@Param("isCustom") Boolean isCustom);
    
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.quiz.id = :quizId AND qq.isCustom = :isCustom")
    List<QuizQuestion> findByQuizIdAndIsCustom(@Param("quizId") Long quizId, @Param("isCustom") Boolean isCustom);
}
