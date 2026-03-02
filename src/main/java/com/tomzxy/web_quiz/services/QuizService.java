package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;

public interface QuizService {
    PageResDTO<?> getAll(int page, int size);

    QuizResDTO getById(Long id);

    QuizResDTO create(QuizReqDTO dto);

    QuizResDTO update(Long id, QuizReqDTO dto);

    void delete(Long id);
}