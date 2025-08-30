package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.AnswerResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.services.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Answer.BASE)
@Tag(name = "Answers", description = "Answer management APIs")
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping()
    @Operation(summary = "Get all answers", description = "Retrieve all answers with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answers retrieved successfully")
    })
    public DataResDTO<PageResDTO<?>> getAllAnswer(
            @Parameter(description = "Page number (0-based)") @RequestParam @Min(0) int page,
            @Parameter(description = "Page size (minimum 10)") @RequestParam @Min(10) int size){
        log.info("Get all Answers");
        return DataResDTO.ok(answerService.get_Answers_pageable(page,size));
    }

    @GetMapping(ApiDefined.Answer.ID)
    @Operation(summary = "Get answer by ID", description = "Retrieve an answer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer found successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    public DataResDTO<AnswerResDTO> getAnswer(
            @Parameter(description = "Answer ID") @PathVariable Long answerId){
        log.info("get Answer by {}", answerId);
        return DataResDTO.ok(answerService.get_Answer(answerId));
    }
    
    @PostMapping()
    @Operation(summary = "Create answer", description = "Create a new answer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Answer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<Void> addAnswer(@Valid @RequestBody AnswerReqDTO answerReqDTO){
        log.info("add Answer");
        answerService.create_Answer(answerReqDTO);
        return DataResDTO.create();
    }

    @PutMapping(ApiDefined.Answer.ID)
    @Operation(summary = "Update answer", description = "Update an existing answer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<AnswerResDTO> updateAnswer(
            @Parameter(description = "Answer ID") @PathVariable Long answerId, 
            @RequestBody @Valid AnswerReqDTO answerReqDTO){
        log.info("Update Answer with id {}", answerId);
        return DataResDTO.update(answerService.update_Answer(answerId,answerReqDTO));
    }

    @DeleteMapping(ApiDefined.Answer.ID)
    @Operation(summary = "Delete answer", description = "Delete an answer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    public DataResDTO<Void> deleteAnswer(
            @Parameter(description = "Answer ID") @PathVariable Long answerId){
        log.info("Delete Answer with id {}", answerId);
        answerService.delete_Answer(answerId);
        return DataResDTO.delete();
    }
}
