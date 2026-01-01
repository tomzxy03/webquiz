package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.services.QuizService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Quiz.BASE)
@Tag(name = "Quizzes", description = "Quiz management APIs")
public class QuizController {
    
    private final QuizService quizService;

    @GetMapping()
    @Operation(summary = "Get all quizzes", description = "Retrieve all quizzes with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllQuizzes(
            @Parameter(description = "Page number (0-based)") @RequestParam @Min(0) int page,
            @Parameter(description = "Page size (minimum 10)") @RequestParam @Min(10) int size) {
        log.info("Get all quizzes");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizService.getAll(page, size)));
    }

    @GetMapping(ApiDefined.Quiz.ID)
    @Operation(summary = "Get quiz by ID", description = "Retrieve a quiz by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz found successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<DataResDTO<QuizResDTO>> getQuiz(
            @Parameter(description = "Quiz ID") @PathVariable Long quizId) {
        log.info("Get quiz by {}", quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(quizService.getById(quizId)));
    }

    @PostMapping()
    @Operation(summary = "Create quiz", description = "Create a new quiz")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Quiz created successfully",
            content = @Content(schema = @Schema(implementation = DataResDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizResDTO>> createQuiz(@Valid @RequestBody QuizReqDTO quizReqDTO) {
        log.info("Create quiz");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create(quizService.create(quizReqDTO)));
    }

    @PutMapping(ApiDefined.Quiz.ID)
    @Operation(summary = "Update quiz", description = "Update an existing quiz")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz updated successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<QuizResDTO>> updateQuiz(
            @Parameter(description = "Quiz ID") @PathVariable Long quizId,
            @Valid @RequestBody QuizReqDTO quizReqDTO) {
        log.info("Update quiz with id {}", quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.update(quizService.update(quizId, quizReqDTO)));
    }

    @DeleteMapping(ApiDefined.Quiz.ID)
    @Operation(summary = "Delete quiz", description = "Delete a quiz by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<DataResDTO<Void>> deleteQuiz(
            @Parameter(description = "Quiz ID") @PathVariable Long quizId) {
        log.info("Delete quiz with id {}", quizId);
        quizService.delete(quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }
}
