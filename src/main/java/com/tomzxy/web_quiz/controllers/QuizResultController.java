package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.dto.requests.QuizResultReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.services.QuizResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
@Tag(name = "Quiz Results", description = "Quiz result management APIs")
public class QuizResultController {

    private final QuizResultService quizResultService;

    // CRUD endpoints
    @PostMapping
    @Operation(summary = "Create quiz result", description = "Create a new quiz result")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Quiz result created successfully",
            content = @Content(schema = @Schema(implementation = DataResDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizResultResDTO>> createQuizResult(@Valid @RequestBody QuizResultReqDTO request) {
        QuizResultResDTO result = quizResultService.createQuizResult(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quiz result", description = "Update an existing quiz result")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz result updated successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz result not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizResultResDTO>> updateQuizResult(
            @Parameter(description = "Quiz result ID") @PathVariable Long id, 
            @Valid @RequestBody QuizResultReqDTO request) {
        QuizResultResDTO result = quizResultService.updateQuizResult(id, request);
        return ResponseEntity.ok(DataResDTO.update(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz result by ID", description = "Retrieve a quiz result by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz result found successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz result not found")
    })
    public ResponseEntity<DataResDTO<QuizResultResDTO>> getQuizResultById(
            @Parameter(description = "Quiz result ID") @PathVariable Long id) {
        QuizResultResDTO result = quizResultService.getQuizResultById(id);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz result", description = "Delete a quiz result by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz result deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz result not found")
    })
    public ResponseEntity<DataResDTO<Void>> deleteQuizResult(
            @Parameter(description = "Quiz result ID") @PathVariable Long id) {
        quizResultService.deleteQuizResult(id);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    @GetMapping
    @Operation(summary = "Get all quiz results", description = "Retrieve all quiz results with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz results retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<QuizResultResDTO>>> getAllQuizResults(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        PageResDTO<QuizResultResDTO> results = quizResultService.getAllQuizResults(page, size);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Business operation endpoints
    @PostMapping("/submit")
    @Operation(summary = "Submit quiz result", description = "Submit a quiz result for evaluation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Quiz result submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizResultResDTO>> submitQuizResult(@Valid @RequestBody QuizResultReqDTO request) {
        QuizResultResDTO result = quizResultService.submitQuizResult(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(result));
    }

    @PostMapping("/calculate/{quizInstanceId}/{userId}")
    @Operation(summary = "Calculate and save result", description = "Calculate and save quiz result for a specific instance and user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Result calculated and saved successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz instance or user not found")
    })
    public ResponseEntity<DataResDTO<QuizResultResDTO>> calculateAndSaveResult(
            @Parameter(description = "Quiz instance ID") @PathVariable Long quizInstanceId, 
            @Parameter(description = "User ID") @PathVariable Long userId) {
        QuizResultResDTO result = quizResultService.calculateAndSaveResult(quizInstanceId, userId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    // User-specific endpoints
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user results", description = "Get all quiz results for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User results retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getUserResults(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<QuizResultResDTO> results = quizResultService.getUserResults(userId);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/user/{userId}/page")
    @Operation(summary = "Get user results with pagination", description = "Get paginated quiz results for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User results retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DataResDTO<PageResDTO<QuizResultResDTO>>> getUserResults(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        PageResDTO<QuizResultResDTO> results = quizResultService.getUserResults(userId, page, size);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/user/{userId}/best")
    @Operation(summary = "Get best result for user", description = "Get the best quiz result for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Best result found successfully"),
        @ApiResponse(responseCode = "404", description = "User not found or no results available")
    })
    public ResponseEntity<DataResDTO<QuizResultResDTO>> getBestResultForUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        return quizResultService.getBestResultForUser(userId)
                .map(result -> ResponseEntity.ok(DataResDTO.ok(result)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<DataResDTO<QuizResultResDTO>> getLatestResultForUser(@PathVariable Long userId) {
        return quizResultService.getLatestResultForUser(userId)
                .map(result -> ResponseEntity.ok(DataResDTO.ok(result)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Quiz-specific endpoints
    @GetMapping("/quiz-instance/{quizInstanceId}")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getQuizInstanceResults(@PathVariable Long quizInstanceId) {
        List<QuizResultResDTO> results = quizResultService.getQuizInstanceResults(quizInstanceId);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/quiz/{quizId}/top")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getTopResultsForQuiz(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "10") int limit) {
        List<QuizResultResDTO> results = quizResultService.getTopResultsForQuiz(quizId, limit);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Filtering endpoints
    @GetMapping("/filter/score-range")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getResultsByScoreRange(
            @RequestParam Double minScore,
            @RequestParam Double maxScore) {
        List<QuizResultResDTO> results = quizResultService.getResultsByScoreRange(minScore, maxScore);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/filter/passed")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getPassedResults(@RequestParam Boolean isPassed) {
        List<QuizResultResDTO> results = quizResultService.getPassedResults(isPassed);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/filter/date-range")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getResultsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<QuizResultResDTO> results = quizResultService.getResultsByDateRange(startDate, endDate);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/filter/high-performing")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getHighPerformingResults(@RequestParam Double minScore) {
        List<QuizResultResDTO> results = quizResultService.getHighPerformingResults(minScore);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/filter/efficient")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getEfficientResults(@RequestParam Integer maxTime) {
        List<QuizResultResDTO> results = quizResultService.getEfficientResults(maxTime);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Analytics endpoints
    @GetMapping("/analytics/user/{userId}/average-score")
    public ResponseEntity<DataResDTO<Double>> getAverageScoreForUser(@PathVariable Long userId) {
        Double averageScore = quizResultService.getAverageScoreForUser(userId);
        return ResponseEntity.ok(DataResDTO.ok(averageScore));
    }

    @GetMapping("/analytics/user/{userId}/pass-rate")
    public ResponseEntity<DataResDTO<Double>> getPassRateForUser(@PathVariable Long userId) {
        Double passRate = quizResultService.getPassRateForUser(userId);
        return ResponseEntity.ok(DataResDTO.ok(passRate));
    }

    @GetMapping("/analytics/quiz/{quizId}/average-score")
    public ResponseEntity<DataResDTO<Double>> getAverageScoreForQuiz(@PathVariable Long quizId) {
        Double averageScore = quizResultService.getAverageScoreForQuiz(quizId);
        return ResponseEntity.ok(DataResDTO.ok(averageScore));
    }

    @GetMapping("/analytics/quiz/{quizId}/pass-rate")
    public ResponseEntity<DataResDTO<Double>> getPassRateForQuiz(@PathVariable Long quizId) {
        Double passRate = quizResultService.getPassRateForQuiz(quizId);
        return ResponseEntity.ok(DataResDTO.ok(passRate));
    }

    // Progress tracking
    @GetMapping("/progress/user/{userId}")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getUserProgress(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sinceDate) {
        List<QuizResultResDTO> results = quizResultService.getUserProgress(userId, sinceDate);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }
} 