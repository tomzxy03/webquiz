package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.AnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.AnswerResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.services.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Answer.BASE)
@Tag(name = "Answers", description = "Answer management APIs")
public class AnswerController {
    private AnswerService answerService;

    @GetMapping()
    @Operation(summary = "Get all answers", description = "Retrieve all answers with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answers retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllAnswer(
            @Parameter(description = "Page number (0-based)") @RequestParam @Min(0) int page,
            @Parameter(description = "Page size (minimum 10)") @RequestParam @Min(10) int size){
        log.info("Get all Answers");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(answerService.get_Answers_pageable(page,size)));
    }

    @GetMapping(ApiDefined.Answer.ID)
    @Operation(summary = "Get answer by ID", description = "Retrieve an answer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer found successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    public ResponseEntity<DataResDTO<AnswerResDTO>> getAnswer(
            @Parameter(description = "Answer ID") @PathVariable Long answerId){
        log.info("get Answer by {}", answerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(answerService.get_Answer(answerId)));
    }
    
    @PostMapping()
    @Operation(summary = "Create answer", description = "Create a new answer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Answer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<AnswerResDTO>> addAnswer(@Valid @RequestBody AnswerReqDTO answerReqDTO){
        log.info("add Answer");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create(answerService.create_Answer(answerReqDTO)));
    }

    @PutMapping(ApiDefined.Answer.ID)
    @Operation(summary = "Update answer", description = "Update an existing answer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<AnswerResDTO>> updateAnswer(
            @Parameter(description = "Answer ID") @PathVariable Long answerId, 
            @RequestBody @Valid AnswerReqDTO answerReqDTO){
        log.info("Update Answer with id {}", answerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.update(answerService.update_Answer(answerId,answerReqDTO)));
    }

    @DeleteMapping(ApiDefined.Answer.ID)
    @Operation(summary = "Delete answer", description = "Delete an answer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    public ResponseEntity<DataResDTO<Object>> deleteAnswer(
            @Parameter(description = "Answer ID") @PathVariable Long answerId){
        log.info("Delete Answer with id {}", answerId);
        answerService.delete_Answer(answerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }
}
