package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.repositories.QuestionRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepo questionRepo;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final QuestionMapper questionMapper;

    @Override
    public PageResDTO<?> get_Questions_pageable(int size, int page) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Question> questions = questionRepo.findAll(pageable);

        return convertToPageResDTO.convertPageResponse(questions,pageable,questionMapper::toQuestionResDTO);

    }

    @Override
    public QuestionResDTO create_Question(QuestionReqDTO questionReqDTO) {

        return null;
    }

    @Override
    public QuestionResDTO update_Question(Long question_id, QuestionReqDTO questionReqDTO) {
        return null;
    }

    @Override
    public QuestionResDTO get_Question(Long question_id) {
        return null;
    }

    @Override
    public void delete_Question(Long question_id) {

    }
}
