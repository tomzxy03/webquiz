package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.AnswerResDTO;
import com.tomzxy.web_quiz.exception.ExistedException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.AnswerMapper;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
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
import org.springframework.transaction.annotation.Transactional;

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
    public void create_Answer(AnswerReqDTO answerReqDTO) {
        boolean flag = answerRepo.existsByAnswerName(answerReqDTO.getAnswerName());
        if(flag){
            throw new ExistedException("Answer has been existed");
        }
        Answer answer = answerMapper.toAnswer(answerReqDTO);
        answerRepo.save(answer);
    }

    @Override
    @Transactional
    public void create_Answers(List<AnswerReqDTO> answerReqDTO) {
        List<Answer> answers = answerMapper.toListAnswer(answerReqDTO);
        List<String> incomingNames = answers.stream()
                        .map(Answer::getAnswerName).toList();
        List<String> existedNames = answerRepo.findAllExistAnswer(incomingNames);
        if(!existedNames.isEmpty()){
            throw  new ExistedException("Answer(s) already exist: "+ existedNames);
        }
        answerRepo.saveAll(answers);
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
}
