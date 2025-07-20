package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.AnswerResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.services.AnswerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Answer.BASE)

public class AnswerController {
    private final AnswerService answerService;

    @GetMapping()
    public DataResDTO<PageResDTO<?>> getAllAnswer(@RequestParam @Min(0) int page ,@RequestParam @Min(10) int size){
        log.info("Get all Answers");
        return DataResDTO.ok(answerService.get_Answers_pageable(page,size));
    }

    @GetMapping(ApiDefined.Answer.ID)
    public DataResDTO<AnswerResDTO> getAnswer(@PathVariable Long answerId){
        log.info("get Answer by {}", answerId);
        return DataResDTO.ok(answerService.get_Answer(answerId));
    }
    @PostMapping()
    public DataResDTO<Void> addAnswer(@Valid @RequestBody AnswerReqDTO answerReqDTO){
        log.info("add Answer");
        answerService.create_Answer(answerReqDTO);
        return DataResDTO.create();
    }

    @PutMapping(ApiDefined.Answer.ID)
    public DataResDTO<AnswerResDTO> updateAnswer(@PathVariable Long answerId, @RequestBody @Valid AnswerReqDTO answerReqDTO){
        log.info("Update Answer with id {}", answerId);
        return DataResDTO.update(answerService.update_Answer(answerId,answerReqDTO));
    }

    @DeleteMapping(ApiDefined.Answer.ID)
    public DataResDTO<Void> deleteAnswer(@PathVariable Long answerId){
        log.info("Delete Answer with id {}", answerId);
        answerService.delete_Answer(answerId);
        return DataResDTO.delete();
    }
}
