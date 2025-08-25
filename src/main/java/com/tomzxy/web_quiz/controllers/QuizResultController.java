package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.dto.requests.QuizResultReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.services.QuizResultService;
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
public class QuizResultController {

    private final QuizResultService quizResultService;

    // CRUD endpoints
    @PostMapping
    public ResponseEntity<DataResDTO<QuizResultResDTO>> createQuizResult(@Valid @RequestBody QuizResultReqDTO request) {
        QuizResultResDTO result = quizResultService.createQuizResult(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResDTO<QuizResultResDTO>> updateQuizResult(
            @PathVariable Long id, 
            @Valid @RequestBody QuizResultReqDTO request) {
        QuizResultResDTO result = quizResultService.updateQuizResult(id, request);
        return ResponseEntity.ok(DataResDTO.update(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResDTO<QuizResultResDTO>> getQuizResultById(@PathVariable Long id) {
        QuizResultResDTO result = quizResultService.getQuizResultById(id);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResDTO<Void>> deleteQuizResult(@PathVariable Long id) {
        quizResultService.deleteQuizResult(id);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    @GetMapping
    public ResponseEntity<DataResDTO<PageResDTO<QuizResultResDTO>>> getAllQuizResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResDTO<QuizResultResDTO> results = quizResultService.getAllQuizResults(page, size);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    // Business operation endpoints
    @PostMapping("/submit")
    public ResponseEntity<DataResDTO<QuizResultResDTO>> submitQuizResult(@Valid @RequestBody QuizResultReqDTO request) {
        QuizResultResDTO result = quizResultService.submitQuizResult(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(result));
    }

    @PostMapping("/calculate/{quizInstanceId}/{userId}")
    public ResponseEntity<DataResDTO<QuizResultResDTO>> calculateAndSaveResult(
            @PathVariable Long quizInstanceId, 
            @PathVariable Long userId) {
        QuizResultResDTO result = quizResultService.calculateAndSaveResult(quizInstanceId, userId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    // User-specific endpoints
    @GetMapping("/user/{userId}")
    public ResponseEntity<DataResDTO<List<QuizResultResDTO>>> getUserResults(@PathVariable Long userId) {
        List<QuizResultResDTO> results = quizResultService.getUserResults(userId);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/user/{userId}/page")
    public ResponseEntity<DataResDTO<PageResDTO<QuizResultResDTO>>> getUserResults(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResDTO<QuizResultResDTO> results = quizResultService.getUserResults(userId, page, size);
        return ResponseEntity.ok(DataResDTO.ok(results));
    }

    @GetMapping("/user/{userId}/best")
    public ResponseEntity<DataResDTO<QuizResultResDTO>> getBestResultForUser(@PathVariable Long userId) {
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