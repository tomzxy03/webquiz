package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.dto.requests.QuizAttemptReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizAttemptResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.services.QuizAttemptService;
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
@RequestMapping("/api/quiz-attempts")
@RequiredArgsConstructor
@Slf4j
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    // CRUD endpoints
    @PostMapping
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> createQuizAttempt(@Valid @RequestBody QuizAttemptReqDTO request) {
        QuizAttemptResDTO result = quizAttemptService.createQuizAttempt(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> updateQuizAttempt(
            @PathVariable Long id, 
            @Valid @RequestBody QuizAttemptReqDTO request) {
        QuizAttemptResDTO result = quizAttemptService.updateQuizAttempt(id, request);
        return ResponseEntity.ok(DataResDTO.update(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> getQuizAttemptById(@PathVariable Long id) {
        QuizAttemptResDTO result = quizAttemptService.getQuizAttemptById(id);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResDTO<Void>> deleteQuizAttempt(@PathVariable Long id) {
        quizAttemptService.deleteQuizAttempt(id);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    @GetMapping
    public ResponseEntity<DataResDTO<PageResDTO<QuizAttemptResDTO>>> getAllQuizAttempts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResDTO<QuizAttemptResDTO> results = quizAttemptService.getAllQuizAttempts(page, size);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Business operation endpoints
    @PostMapping("/start/{quizId}/{userId}")
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> startQuizAttempt(
            @PathVariable Long quizId, 
            @PathVariable Long userId) {
        QuizAttemptResDTO result = quizAttemptService.startQuizAttempt(quizId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(result));
    }

    @PostMapping("/resume/{attemptId}")
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> resumeQuizAttempt(@PathVariable Long attemptId) {
        QuizAttemptResDTO result = quizAttemptService.resumeQuizAttempt(attemptId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @PutMapping("/{attemptId}/progress")
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> updateAttemptProgress(
            @PathVariable Long attemptId,
            @RequestParam Integer currentQuestionIndex,
            @RequestParam String answersSnapshot) {
        QuizAttemptResDTO result = quizAttemptService.updateAttemptProgress(attemptId, currentQuestionIndex, answersSnapshot);
        return ResponseEntity.ok(DataResDTO.update(result));
    }

    @PostMapping("/{attemptId}/complete")
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> completeQuizAttempt(@PathVariable Long attemptId) {
        QuizAttemptResDTO result = quizAttemptService.completeQuizAttempt(attemptId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @PostMapping("/{attemptId}/abandon")
    public ResponseEntity<DataResDTO<QuizAttemptResDTO>> abandonQuizAttempt(@PathVariable Long attemptId) {
        QuizAttemptResDTO result = quizAttemptService.abandonQuizAttempt(attemptId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    // User-specific endpoints
    @GetMapping("/user/{userId}")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getUserAttempts(@PathVariable Long userId) {
        List<QuizAttemptResDTO> results = quizAttemptService.getUserAttempts(userId);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/user/{userId}/page")
    public ResponseEntity<DataResDTO<PageResDTO<QuizAttemptResDTO>>> getUserAttempts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResDTO<QuizAttemptResDTO> results = quizAttemptService.getUserAttempts(userId, page, size);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/user/{userId}/resumable")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getResumableAttempts(@PathVariable Long userId) {
        List<QuizAttemptResDTO> results = quizAttemptService.getResumableAttempts(userId);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Quiz-specific endpoints
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getQuizAttempts(@PathVariable Long quizId) {
        List<QuizAttemptResDTO> results = quizAttemptService.getQuizAttempts(quizId);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/quiz/{quizId}/user/{userId}")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getUserQuizAttempts(
            @PathVariable Long quizId, 
            @PathVariable Long userId) {
        List<QuizAttemptResDTO> results = quizAttemptService.getUserQuizAttempts(userId, quizId);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Status-based endpoints
    @GetMapping("/status/{status}")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getAttemptsByStatus(@PathVariable String status) {
        List<QuizAttemptResDTO> results = quizAttemptService.getAttemptsByStatus(status);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/status/{status}/page")
    public ResponseEntity<DataResDTO<PageResDTO<QuizAttemptResDTO>>> getAttemptsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResDTO<QuizAttemptResDTO> results = quizAttemptService.getAttemptsByStatus(status, page, size);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/in-progress")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getInProgressAttempts() {
        List<QuizAttemptResDTO> results = quizAttemptService.getInProgressAttempts();
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/completed")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getCompletedAttempts() {
        List<QuizAttemptResDTO> results = quizAttemptService.getCompletedAttempts();
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/abandoned")
    public ResponseEntity<DataResDTO<List<QuizAttemptResDTO>>> getAbandonedAttempts() {
        List<QuizAttemptResDTO> results = quizAttemptService.getAbandonedAttempts();
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Analytics endpoints
    @GetMapping("/analytics/user/{userId}/count")
    public ResponseEntity<DataResDTO<Long>> getAttemptCountForUser(@PathVariable Long userId) {
        Long count = quizAttemptService.getAttemptCountForUser(userId);
        return ResponseEntity.ok(DataResDTO.ok(count));
    }

    @GetMapping("/analytics/quiz/{quizId}/count")
    public ResponseEntity<DataResDTO<Long>> getAttemptCountForQuiz(@PathVariable Long quizId) {
        Long count = quizAttemptService.getAttemptCountForQuiz(quizId);
        return ResponseEntity.ok(DataResDTO.ok(count));
    }

    @GetMapping("/analytics/user/{userId}/completion-rate")
    public ResponseEntity<DataResDTO<Double>> getCompletionRateForUser(@PathVariable Long userId) {
        Double rate = quizAttemptService.getCompletionRateForUser(userId);
        return ResponseEntity.ok(DataResDTO.ok(rate));
    }

    @GetMapping("/analytics/quiz/{quizId}/completion-rate")
    public ResponseEntity<DataResDTO<Double>> getCompletionRateForQuiz(@PathVariable Long quizId) {
        Double rate = quizAttemptService.getCompletionRateForQuiz(quizId);
        return ResponseEntity.ok(DataResDTO.ok(rate));
    }
} 