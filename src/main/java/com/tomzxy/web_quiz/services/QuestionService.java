package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;

public interface QuestionService {
    PageResDTO<?> get_Questions_pageable(int size , int page);

    QuestionResDTO create_Question(QuestionReqDTO questionReqDTO);

    QuestionResDTO update_Question(Long question_id, QuestionReqDTO questionReqDTO);

    QuestionResDTO get_Question(Long question_id);

    void delete_Question(Long question_id);
}
