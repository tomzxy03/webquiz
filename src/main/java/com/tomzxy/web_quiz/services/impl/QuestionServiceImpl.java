package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;
import com.tomzxy.web_quiz.exception.ExistedException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.AnswerMapper;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
import com.tomzxy.web_quiz.models.Answer;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepo questionRepo;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;

    @Override
    public PageResDTO<?> get_Questions_pageable(int size, int page) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Question> questions = questionRepo.findAll(pageable);

        // config to use mapstruct
        return convertToPageResDTO.convertPageResponse(questions,pageable,questionMapper::toQuestionResDTO);

    }

    @Override
    public void create_Question(QuestionReqDTO questionReqDTO) {
        boolean flag = questionRepo.existsByQuestionName(questionReqDTO.getQuestionName());
        if(flag){
            throw new ExistedException("Question has been existed");
        }
        Question question = questionMapper.toQuestion(questionReqDTO);
        Set<Answer> answers = answerMapper.toListAnswer(questionReqDTO.getAnswerReqDTOList());
        question.saveAnswers(answers);

        questionRepo.save(question);
    }

    @Override
    @Transactional
    public void create_Questions(List<QuestionReqDTO> questionReqDTO) {
        List<Question> questions = questionMapper.toListQuestion(questionReqDTO);
        List<String> incomingNames = questions.stream()
                        .map(Question::getQuestionName).toList();
        List<String> existedNames = questionRepo.findAllExistQuestion(incomingNames);
        if(!existedNames.isEmpty()){
            throw  new ExistedException("Question(s) already exist: "+ existedNames);
        }
        questionRepo.saveAll(questions);
    }

    @Override
    public QuestionResDTO update_Question(Long question_id, QuestionReqDTO questionReqDTO) {
        Question question = findQuestion(question_id);
        questionMapper.updateQuestion(question,questionReqDTO);
        
        return null;
    }

    @Override
    public QuestionResDTO get_Question(Long question_id) {
        return null;
    }

    @Override
    public void delete_Question(Long question_id) {

    }
    public Question findQuestion(Long question_id){
        return questionRepo.findById(question_id).orElseThrow(()->new NotFoundException("Question not found!"));
    }
}
