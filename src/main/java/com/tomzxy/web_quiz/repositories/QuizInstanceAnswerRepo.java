package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.QuizInstanceAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizInstanceAnswerRepo extends JpaRepository<QuizInstanceAnswer, Long> {
    
    // Tìm đáp án theo quiz instance question, sắp xếp theo thứ tự hiển thị
    List<QuizInstanceAnswer> findByQuizInstanceQuestionIdOrderByDisplayOrder(Long quizInstanceQuestionId);

    List<QuizInstanceAnswer> findByQuizInstanceQuestionId(Long quizInstanceQuestionId);
    

    List<QuizInstanceAnswer> findByQuizInstanceQuestionIdAndIsCorrectTrue(Long quizInstanceQuestionId);
    

    List<QuizInstanceAnswer> findByOriginalAnswerId(Long originalAnswerId);
} 