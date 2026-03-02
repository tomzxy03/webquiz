package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizSubmissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;

public interface QuizInstanceService {

  QuizInstanceResDTO createQuizInstance(QuizInstanceReqDTO request);

  QuizInstanceResDTO getQuizInstance(Long instanceId, Long userId);

  PageResDTO<?> getAllQuizInstances(int page, int size);

  PageResDTO<?> getAllQuizInstancesByLobbyId(Long lobbyId, int page, int size);

  PageResDTO<?> getAllQuizInstancesByUserId(Long userId, int page, int size);

  PageResDTO<?> getAllQuizInstancesByQuizId(Long quizId, int page, int size);

  PageResDTO<?> getAllQuizInstancesByQuizIdAndUserId(Long quizId, Long userId, int page, int size);

  boolean canUserStartQuiz(Long quizId, Long userId);

  QuizResultDetailResDTO submitQuiz(QuizSubmissionReqDTO request);

  QuizResultDetailResDTO getQuizResult(Long instanceId, Long userId);

  void handleTimedOutInstances();

  void deleteQuizInstance(Long instanceId, Long userId);
}