package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.answer.AnswerResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.AnswerMapper;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.repositories.AnswerRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.AnswerService;
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
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepo answerRepo;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final AnswerMapper answerMapper;

    @Override
    public PageResDTO<?> get_Answers_pageable(int size, int page) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Answer> answers = answerRepo.findAll(pageable);

        // config to use mapstruct
        return convertToPageResDTO.convertPageResponse(answers,pageable,answerMapper::toAnswerResDTO);

    }

    @Override
    public AnswerResDTO update_Answer(Long answer_id, AnswerReqDTO answerReqDTO) {
        Answer answer = findAnswer(answer_id);
        answerMapper.updateAnswer(answer,answerReqDTO);
        
        return null;
    }

    @Override
    public AnswerResDTO get_Answer(Long answer_id) {
        return null;
    }

    @Override
    public void delete_Answer(Long answer_id) {

    }
    public Answer findAnswer(Long answer_id){
        return answerRepo.findById(answer_id).orElseThrow(()->new NotFoundException("Answer not found!"));
    }

    @Override
    public AnswerResDTO create_Answer(AnswerReqDTO answerReqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create_Answer'");
    }

    @Override
    public void create_Answers(List<AnswerReqDTO> answerReqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create_Answers'");
    }
}
