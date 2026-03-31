package com.tomzxy.web_quiz.services;

import org.springframework.security.core.Authentication;

import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.models.IdentityContext;

public interface AttemptService {
    PageResDTO<?> getMyAttempts(IdentityContext identity, int page, int size);
    PageResDTO<?> getQuizAttempts(Long quizId, Long groupId, int page, int size);
    QuizResultDetailResDTO getAttemptDetail(Long instanceId, Authentication auth);
}
