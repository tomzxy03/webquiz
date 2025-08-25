package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizSubmissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultDetailResDTO;

public interface QuizInstanceService {
    

    QuizInstanceResDTO createQuizInstance(QuizInstanceReqDTO request);
    

    QuizInstanceResDTO getQuizInstance(Long instanceId, Long userId);
    
  
    QuizResultDetailResDTO submitQuiz(QuizSubmissionReqDTO request);
    

    QuizResultDetailResDTO getQuizResult(Long instanceId, Long userId);
    
   
    boolean canUserStartQuiz(Long quizId, Long userId);
    

    void handleTimedOutInstances();
    

    void deleteQuizInstance(Long instanceId, Long userId);
} 