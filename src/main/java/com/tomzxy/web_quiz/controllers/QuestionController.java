package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;

import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;
import com.tomzxy.web_quiz.services.QuestionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Question.BASE)

public class QuestionController {
    private final QuestionService questionService;

    @GetMapping()
    public DataResDTO<PageResDTO<?>> getAllQuestion(@RequestParam @Min(0) int page ,@RequestParam @Min(10) int size){
        log.info("Get all Questions");
        return DataResDTO.ok(questionService.get_Questions_pageable(page,size));
    }

    @GetMapping(ApiDefined.Question.ID)
    public DataResDTO<QuestionResDTO> getQuestion(@PathVariable Long questionId){
        log.info("get Question by {}", questionId);
        return DataResDTO.ok(questionService.get_Question(questionId));
    }
    @PostMapping()
    public DataResDTO<Void> addQuestion(@Valid @RequestBody QuestionReqDTO questionReqDTO){
        log.info("add Question");
        questionService.create_Question(questionReqDTO);
        return DataResDTO.create();
    }
    @PostMapping(ApiDefined.Question.ADD_LIST)
    public DataResDTO<Void> addQuestionList(@Valid @RequestBody List<QuestionReqDTO> questionReqDTOList){
        log.info("add Question List");
        questionService.create_Questions(questionReqDTOList);
        return DataResDTO.create();
    }

    @PutMapping(ApiDefined.Question.ID)
    public DataResDTO<QuestionResDTO> updateQuestion(@PathVariable Long questionId, @RequestBody @Valid QuestionReqDTO questionReqDTO){
        log.info("Update Question with id {}", questionId);
        return DataResDTO.update(questionService.update_Question(questionId,questionReqDTO));
    }

    @DeleteMapping(ApiDefined.Question.ID)
    public DataResDTO<Void> deleteQuestion(@PathVariable Long questionId){
        log.info("Delete Question with id {}", questionId);
        questionService.delete_Question(questionId);
        return DataResDTO.delete();
    }
}
