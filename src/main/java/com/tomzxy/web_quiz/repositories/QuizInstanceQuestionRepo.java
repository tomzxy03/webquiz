package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.QuizInstanceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizInstanceQuestionRepo extends JpaRepository<QuizInstanceQuestion, Long> {
    
    // Tìm câu hỏi theo quiz instance, sắp xếp theo thứ tự hiển thị
    List<QuizInstanceQuestion> findByQuizInstanceIdOrderByDisplayOrder(Long quizInstanceId);
    
   
    List<QuizInstanceQuestion> findByQuizInstanceId(Long quizInstanceId);
    

    List<QuizInstanceQuestion> findByOriginalQuestionId(Long originalQuestionId);
    

    long countByQuizInstanceId(Long quizInstanceId);
} 