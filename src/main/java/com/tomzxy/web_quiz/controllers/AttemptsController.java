package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.dto.requests.CreateAttemptReqDTO;
import com.tomzxy.web_quiz.dto.responses.AttemptDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.AttemptResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.UserStatisticsResDTO;
import com.tomzxy.web_quiz.services.AttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/attempts")
@Tag(name = "Attempts", description = "Exam attempt management APIs")
public class AttemptsController {

    private final AttemptService attemptService;

    @GetMapping
    @Operation(summary = "Get all exam attempts", description = "Retrieve all exam attempts with optional filters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<AttemptResDTO>>> getAllAttempts(
            @Parameter(description = "User ID filter") @RequestParam(required = false) Long userId,
            @Parameter(description = "Quiz ID filter") @RequestParam(required = false) Long quizId) {
        log.info("Get all attempts - userId: {}, quizId: {}", userId, quizId);
        List<AttemptResDTO> attempts = attemptService.getAllAttempts(userId, quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(attempts));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exam attempt detail by ID", description = "Retrieve detailed information about a specific attempt")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attempt found successfully"),
        @ApiResponse(responseCode = "404", description = "Attempt not found")
    })
    public ResponseEntity<DataResDTO<AttemptDetailResDTO>> getAttemptById(
            @Parameter(description = "Attempt ID") @PathVariable Long id) {
        log.info("Get attempt by id: {}", id);
        AttemptDetailResDTO attempt = attemptService.getAttemptById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(attempt));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all attempts by user", description = "Retrieve all attempts for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<AttemptResDTO>>> getAttemptsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Get attempts by user: {}", userId);
        List<AttemptResDTO> attempts = attemptService.getAttemptsByUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(attempts));
    }

    @GetMapping("/quiz/{quizId}/user/{userId}")
    @Operation(summary = "Get attempts for specific quiz and user", description = "Retrieve attempts for a specific quiz and user combination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<AttemptResDTO>>> getAttemptsByQuizAndUser(
            @Parameter(description = "Quiz ID") @PathVariable Long quizId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Get attempts by quiz: {} and user: {}", quizId, userId);
        List<AttemptResDTO> attempts = attemptService.getAttemptsByQuizAndUser(quizId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(attempts));
    }

    @PostMapping
    @Operation(summary = "Create new exam attempt", description = "Create a new exam attempt with answers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Attempt created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<DataResDTO<AttemptDetailResDTO>> createAttempt(
            @Valid @RequestBody CreateAttemptReqDTO request) {
        log.info("Create attempt for quiz: {}", request.getQuizId());
        AttemptDetailResDTO attempt = attemptService.createAttempt(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create(attempt));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exam attempt", description = "Delete an exam attempt by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attempt deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Attempt not found")
    })
    public ResponseEntity<DataResDTO<Object>> deleteAttempt(
            @Parameter(description = "Attempt ID") @PathVariable Long id) {
        log.info("Delete attempt: {}", id);
        attemptService.deleteAttempt(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }

    @GetMapping("/user/{userId}/statistics")
    @Operation(summary = "Get user statistics", description = "Retrieve statistics for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<DataResDTO<UserStatisticsResDTO>> getUserStatistics(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Get statistics for user: {}", userId);
        UserStatisticsResDTO statistics = attemptService.getUserStatistics(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(statistics));
    }
}
