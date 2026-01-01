package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;

import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionResDTO;
import com.tomzxy.web_quiz.services.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Question.BASE)
@Tag(name = "Questions", description = "Question management APIs")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping()
    @Operation(summary = "Get all questions", description = "Retrieve all questions with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllQuestion(
            @Parameter(description = "Page number (0-based)") @RequestParam @Min(0) int page,
            @Parameter(description = "Page size (minimum 10)") @RequestParam @Min(10) int size){
        log.info("Get all Questions");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(questionService.get_Questions_pageable(page,size)));
    }

    @GetMapping(ApiDefined.Question.ID)
    @Operation(summary = "Get question by ID", description = "Retrieve a question by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question found successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<DataResDTO<QuestionResDTO>> getQuestion(
            @Parameter(description = "Question ID") @PathVariable Long questionId){
        log.info("get Question by {}", questionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(questionService.get_Question(questionId)));
    }
    
    @PostMapping("")
    @Operation(summary = "Create question", description = "Create a new question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Question created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<Void>> addQuestion(@Valid @RequestBody QuestionReqDTO questionReqDTO){
        log.info("add Question");
        questionService.create_Question(questionReqDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create());
    }
    
    @PostMapping(ApiDefined.Question.ADD_LIST)
    @Operation(summary = "Create multiple questions", description = "Create multiple questions at once")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Questions created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<Void>> addQuestionList(@Valid @RequestBody List<QuestionReqDTO> questionReqDTOList){
        log.info("add Question List");
        questionService.create_Questions(questionReqDTOList);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create());
    }

    @PutMapping(ApiDefined.Question.ID)
    @Operation(summary = "Update question", description = "Update an existing question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question updated successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuestionResDTO>> updateQuestion(
            @Parameter(description = "Question ID") @PathVariable Long questionId, 
            @RequestBody @Valid QuestionReqDTO questionReqDTO){
        log.info("Update Question with id {}", questionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.update(questionService.update_Question(questionId,questionReqDTO)));
    }

    @DeleteMapping(ApiDefined.Question.ID)
    @Operation(summary = "Delete question", description = "Delete a question by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<DataResDTO<Void>> deleteQuestion(
            @Parameter(description = "Question ID") @PathVariable Long questionId){
        log.info("Delete Question with id {}", questionId);
        questionService.delete_Question(questionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }
}
