package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.answer.AnswerResDTO;

import java.util.List;

public interface AnswerService {
    PageResDTO<?> get_Answers_pageable(int size , int page);

    AnswerResDTO create_Answer(AnswerReqDTO answerReqDTO);

    void create_Answers(List<AnswerReqDTO> answerReqDTO);

    AnswerResDTO update_Answer(Long answer_id, AnswerReqDTO answerReqDTO);

    AnswerResDTO get_Answer(Long answer_id);

    void delete_Answer(Long answer_id);
}
