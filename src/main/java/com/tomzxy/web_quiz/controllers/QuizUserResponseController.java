package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.dto.requests.QuizUserResponseReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizUserResponseResDTO;
import com.tomzxy.web_quiz.services.QuizUserResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/quiz-user-responses")
@Tag(name = "Quiz User Responses", description = "Quiz user response management APIs")
public class QuizUserResponseController {

    private final QuizUserResponseService quizUserResponseService;

    // CRUD operations
    @PostMapping
    @Operation(summary = "Create user response", description = "Create a new quiz user response")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User response created successfully",
            content = @Content(schema = @Schema(implementation = DataResDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizUserResponseResDTO>> createUserResponse(@Valid @RequestBody QuizUserResponseReqDTO request) {
        log.info("Create user response");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create(quizUserResponseService.createUserResponse(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user response", description = "Update an existing quiz user response")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User response updated successfully"),
        @ApiResponse(responseCode = "404", description = "User response not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizUserResponseResDTO>> updateUserResponse(
            @Parameter(description = "User response ID") @PathVariable Long id,
            @Valid @RequestBody QuizUserResponseReqDTO request) {
        log.info("Update user response with id {}", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.update(quizUserResponseService.updateUserResponse(id, request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user response by ID", description = "Retrieve a quiz user response by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User response found successfully"),
        @ApiResponse(responseCode = "404", description = "User response not found")
    })
    public ResponseEntity<DataResDTO<QuizUserResponseResDTO>> getUserResponseById(
            @Parameter(description = "User response ID") @PathVariable Long id) {
        log.info("Get user response by {}", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getUserResponseById(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user response", description = "Delete a quiz user response by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User response deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User response not found")
    })
    public ResponseEntity<DataResDTO<Void>> deleteUserResponse(
            @Parameter(description = "User response ID") @PathVariable Long id) {
        log.info("Delete user response with id {}", id);
        quizUserResponseService.deleteUserResponse(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }

    @GetMapping
    @Operation(summary = "Get all user responses", description = "Retrieve all quiz user responses with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllUserResponses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Get all user responses");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getAllUserResponses(page, size)));
    }

    // Business operations
    @PostMapping("/submit")
    @Operation(summary = "Submit answer", description = "Submit an answer for a quiz question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizUserResponseResDTO>> submitAnswer(
            @Parameter(description = "Quiz instance question ID") @RequestParam Long quizInstanceQuestionId,
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Selected answer ID") @RequestParam(required = false) Long selectedAnswerId,
            @Parameter(description = "User answer text") @RequestParam(required = false) String userAnswer) {
        log.info("Submit answer for question {}", quizInstanceQuestionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.submitAnswer(quizInstanceQuestionId, userId, selectedAnswerId, userAnswer)));
    }

    @PutMapping("/{responseId}/answer")
    @Operation(summary = "Update answer", description = "Update an existing answer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Response not found")
    })
    public ResponseEntity<DataResDTO<QuizUserResponseResDTO>> updateAnswer(
            @Parameter(description = "Response ID") @PathVariable Long responseId,
            @Parameter(description = "Selected answer ID") @RequestParam(required = false) Long selectedAnswerId,
            @Parameter(description = "User answer text") @RequestParam(required = false) String userAnswer) {
        log.info("Update answer for response {}", responseId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.updateAnswer(responseId, selectedAnswerId, userAnswer)));
    }

    @PostMapping("/skip")
    @Operation(summary = "Skip question", description = "Skip a quiz question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question skipped successfully")
    })
    public ResponseEntity<DataResDTO<QuizUserResponseResDTO>> skipQuestion(
            @Parameter(description = "Quiz instance question ID") @RequestParam Long quizInstanceQuestionId,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        log.info("Skip question {}", quizInstanceQuestionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.skipQuestion(quizInstanceQuestionId, userId)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user responses", description = "Get all responses for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getUserResponses(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Get responses for user {}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getUserResponses(userId)));
    }

    @GetMapping("/user/{userId}/page")
    @Operation(summary = "Get user responses with pagination", description = "Get paginated responses for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getUserResponses(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Get paginated responses for user {}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getUserResponses(userId, page, size)));
    }

    @GetMapping("/quiz-instance/{quizInstanceId}")
    @Operation(summary = "Get quiz instance responses", description = "Get all responses for a specific quiz instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz instance responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getQuizInstanceResponses(
            @Parameter(description = "Quiz instance ID") @PathVariable Long quizInstanceId) {
        log.info("Get responses for quiz instance {}", quizInstanceId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getQuizInstanceResponses(quizInstanceId)));
    }

    @GetMapping("/quiz-instance/{quizInstanceId}/user/{userId}")
    @Operation(summary = "Get user quiz instance responses", description = "Get all responses for a specific user in a quiz instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User quiz instance responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getUserQuizInstanceResponses(
            @Parameter(description = "Quiz instance ID") @PathVariable Long quizInstanceId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Get responses for user {} in quiz instance {}", userId, quizInstanceId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getUserQuizInstanceResponses(quizInstanceId, userId)));
    }

    @GetMapping("/correct")
    @Operation(summary = "Get correct responses", description = "Get all correct or incorrect responses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Correct responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getCorrectResponses(
            @Parameter(description = "Whether to get correct responses") @RequestParam Boolean isCorrect) {
        log.info("Get {} responses", isCorrect ? "correct" : "incorrect");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getCorrectResponses(isCorrect)));
    }

    @GetMapping("/correct/page")
    @Operation(summary = "Get correct responses with pagination", description = "Get paginated correct or incorrect responses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Correct responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getCorrectResponses(
            @Parameter(description = "Whether to get correct responses") @RequestParam Boolean isCorrect,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Get paginated {} responses", isCorrect ? "correct" : "incorrect");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getCorrectResponses(isCorrect, page, size)));
    }

    @GetMapping("/time-range")
    @Operation(summary = "Get responses by time range", description = "Get responses within a specific time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getResponsesByTimeRange(
            @Parameter(description = "Minimum time in seconds") @RequestParam(required = false) Integer minTime,
            @Parameter(description = "Maximum time in seconds") @RequestParam(required = false) Integer maxTime) {
        log.info("Get responses by time range: {} - {}", minTime, maxTime);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getResponsesByTimeRange(minTime, maxTime)));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get responses by date range", description = "Get responses within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getResponsesByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Get responses by date range: {} - {}", startDate, endDate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getResponsesByDateRange(startDate, endDate)));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent responses", description = "Get responses since a specific date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent responses retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getRecentResponses(
            @Parameter(description = "Since date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sinceDate) {
        log.info("Get recent responses since {}", sinceDate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getRecentResponses(sinceDate)));
    }

    @GetMapping("/skipped")
    @Operation(summary = "Get skipped questions", description = "Get all skipped questions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Skipped questions retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getSkippedQuestions() {
        log.info("Get skipped questions");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getSkippedQuestions()));
    }

    @GetMapping("/answered")
    @Operation(summary = "Get answered questions", description = "Get all answered questions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answered questions retrieved successfully")
    })
    public ResponseEntity<DataResDTO<List<QuizUserResponseResDTO>>> getAnsweredQuestions() {
        log.info("Get answered questions");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizUserResponseService.getAnsweredQuestions()));
    }
} 