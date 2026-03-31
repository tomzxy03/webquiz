package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizStateResDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizAnswerReqDTO;
import com.tomzxy.web_quiz.models.IdentityContext;

public interface QuizInstanceService {

  void saveAnswer(Long instanceId, IdentityContext identity, QuizAnswerReqDTO request);

  QuizInstanceResDTO createQuizInstance(QuizInstanceReqDTO request, IdentityContext identity);

  QuizInstanceResDTO createQuizInstance(Long quizId, IdentityContext identity);

  QuizInstanceResDTO getQuizInstance(Long instanceId, IdentityContext identity);

  QuizStateResDTO getQuizState(Long instanceId, IdentityContext identity);

  PageResDTO<?> getAllQuizInstances(int page, int size);

  PageResDTO<?> getAllQuizInstancesByLobbyId(Long lobbyId, int page, int size);

  PageResDTO<?> getAllQuizInstancesByUserId(Long userId, int page, int size);

  PageResDTO<?> getAllQuizInstancesByQuizId(Long quizId, int page, int size);

  PageResDTO<?> getAllQuizInstancesByQuizIdAndUserId(Long quizId, Long userId, int page, int size);

  boolean canUserStartQuiz(Long quizId, IdentityContext identity);

  QuizResultDetailResDTO submitQuiz(Long instanceId, IdentityContext identity);

  QuizResultDetailResDTO getQuizResult(Long instanceId, IdentityContext identity);

  QuizResultDetailResDTO getQuizResultForHost(Long instanceId);

  void handleTimedOutInstances();

  void deleteQuizInstance(Long instanceId, IdentityContext identity);
}
