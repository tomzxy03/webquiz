package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.AttemptDetailResDTO;
import com.tomzxy.web_quiz.models.IdentityContext;

public interface AttemptService {
    PageResDTO<?> getMyAttempts(IdentityContext identity, int page, int size);
    PageResDTO<?> getQuizAttempts(Long quizId, Long groupId, int page, int size);
    AttemptDetailResDTO getMyAttemptDetail(Long instanceId);
    AttemptDetailResDTO getSubmissionDetail(Long groupId, Long quizId, Long instanceId);
}
