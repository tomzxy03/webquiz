package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizSubmissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz-instances")
@RequiredArgsConstructor
@Tag(name = "Quiz Instances", description = "Quiz instance management APIs")
public class QuizInstanceController {

    private final QuizInstanceService quizInstanceService;

    @PostMapping("/start")
    @Operation(summary = "Start quiz instance", description = "Start a new quiz instance for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz instance started successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Quiz or user not found")
    })
    public ResponseEntity<DataResDTO<QuizInstanceResDTO>> startQuiz(@RequestBody QuizInstanceReqDTO request) {
        QuizInstanceResDTO instance = quizInstanceService.createQuizInstance(request);
        return ResponseEntity.ok(DataResDTO.ok(instance));
    }

    @GetMapping("/{instanceId}")
    @Operation(summary = "Get quiz instance", description = "Retrieve a quiz instance by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz instance found successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz instance not found")
    })
    public ResponseEntity<DataResDTO<QuizInstanceResDTO>> getQuizInstance(
            @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        QuizInstanceResDTO instance = quizInstanceService.getQuizInstance(instanceId, userId);
        return ResponseEntity.ok(DataResDTO.ok(instance));
    }

    @PostMapping("/{instanceId}/submit")
    @Operation(summary = "Submit quiz", description = "Submit answers for a quiz instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Quiz instance not found")
    })
    public ResponseEntity<DataResDTO<QuizResultDetailResDTO>> submitQuiz(
            @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
            @RequestBody QuizSubmissionReqDTO request) {
        request.setQuizInstanceId(instanceId);
        QuizResultDetailResDTO result = quizInstanceService.submitQuiz(request);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @GetMapping("/{instanceId}/result")
    @Operation(summary = "Get quiz result", description = "Get the result of a completed quiz instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz result retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz instance or result not found")
    })
    public ResponseEntity<DataResDTO<QuizResultDetailResDTO>> getQuizResult(
            @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        QuizResultDetailResDTO result = quizInstanceService.getQuizResult(instanceId, userId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @DeleteMapping("/{instanceId}")
    @Operation(summary = "Delete quiz instance", description = "Delete a quiz instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz instance deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz instance not found")
    })
    public ResponseEntity<DataResDTO<Void>> deleteQuizInstance(
            @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        quizInstanceService.deleteQuizInstance(instanceId, userId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    @GetMapping("/check-eligibility")
    @Operation(summary = "Check user eligibility", description = "Check if a user can start a specific quiz")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eligibility check completed successfully")
    })
    public ResponseEntity<DataResDTO<Boolean>> checkUserEligibility(
            @Parameter(description = "Quiz ID") @RequestParam Long quizId,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        boolean canStart = quizInstanceService.canUserStartQuiz(quizId, userId);
        return ResponseEntity.ok(DataResDTO.ok(canStart));
    }
} 