package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizSubmissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz-instances")
@RequiredArgsConstructor
public class QuizInstanceController {

    private final QuizInstanceService quizInstanceService;

    @PostMapping("/start")
    public ResponseEntity<DataResDTO<QuizInstanceResDTO>> startQuiz(@RequestBody QuizInstanceReqDTO request) {
        QuizInstanceResDTO instance = quizInstanceService.createQuizInstance(request);
        return ResponseEntity.ok(DataResDTO.ok(instance));
    }

    @GetMapping("/{instanceId}")
    public ResponseEntity<DataResDTO<QuizInstanceResDTO>> getQuizInstance(
            @PathVariable Long instanceId,
            @RequestParam Long userId) {
        QuizInstanceResDTO instance = quizInstanceService.getQuizInstance(instanceId, userId);
        return ResponseEntity.ok(DataResDTO.ok(instance));
    }

    @PostMapping("/{instanceId}/submit")
    public ResponseEntity<DataResDTO<QuizResultDetailResDTO>> submitQuiz(
            @PathVariable Long instanceId,
            @RequestBody QuizSubmissionReqDTO request) {
        request.setQuizInstanceId(instanceId);
        QuizResultDetailResDTO result = quizInstanceService.submitQuiz(request);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @GetMapping("/{instanceId}/result")
    public ResponseEntity<DataResDTO<QuizResultDetailResDTO>> getQuizResult(
            @PathVariable Long instanceId,
            @RequestParam Long userId) {
        QuizResultDetailResDTO result = quizInstanceService.getQuizResult(instanceId, userId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @DeleteMapping("/{instanceId}")
    public ResponseEntity<DataResDTO<Void>> deleteQuizInstance(
            @PathVariable Long instanceId,
            @RequestParam Long userId) {
        quizInstanceService.deleteQuizInstance(instanceId, userId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    @GetMapping("/check-eligibility")
    public ResponseEntity<DataResDTO<Boolean>> checkUserEligibility(
            @RequestParam Long quizId,
            @RequestParam Long userId) {
        boolean canStart = quizInstanceService.canUserStartQuiz(quizId, userId);
        return ResponseEntity.ok(DataResDTO.ok(canStart));
    }
} 