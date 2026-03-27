package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.configs.IdentityResolver;
import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizAnswerReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizStateResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.enums.IdentityType;
import com.tomzxy.web_quiz.models.IdentityContext;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiDefined.QuizInstance.BASE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Instances", description = "Quiz instance management APIs")
public class QuizInstanceController {

        private final QuizInstanceService quizInstanceService;
        private final IdentityResolver identityResolver;

        private IdentityContext resolveIdentity(Long userId, HttpServletRequest request) {
                if (userId != null) {
                        return new IdentityContext(IdentityType.USER, String.valueOf(userId));
                }
                return identityResolver.resolve(request);
        }

        @PostMapping(ApiDefined.QuizInstance.START)
        @Operation(summary = "Start quiz instance", description = "Start a new quiz instance for a user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz instance started successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "404", description = "Quiz or user not found")
        })
        public ResponseEntity<DataResDTO<QuizInstanceResDTO>> startQuiz(
                        @RequestBody QuizInstanceReqDTO request,
                        HttpServletRequest httpServletRequest) {
                IdentityContext identity = identityResolver.resolve(httpServletRequest);
                QuizInstanceResDTO instance = quizInstanceService.createQuizInstance(request, identity);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(DataResDTO.create(instance));
        }

        @GetMapping(ApiDefined.QuizInstance.ID)
        @Operation(summary = "Get quiz instance", description = "Retrieve a quiz instance by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz instance found successfully"),
                        @ApiResponse(responseCode = "404", description = "Quiz instance not found")
        })
        public ResponseEntity<DataResDTO<QuizInstanceResDTO>> getQuizInstance(
                        @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
                        @Parameter(description = "User ID") @RequestParam(required = false) Long userId,
                        HttpServletRequest httpServletRequest) {
                IdentityContext identity = resolveIdentity(userId, httpServletRequest);
                QuizInstanceResDTO instance = quizInstanceService.getQuizInstance(instanceId, identity);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(instance));
        }

        @GetMapping(ApiDefined.QuizInstance.STATE)
        @Operation(summary = "Get quiz state (resume)", description = "Get current quiz state for resume - returns questions with saved answers and remaining time")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz state retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Quiz instance not found"),
                        @ApiResponse(responseCode = "400", description = "Quiz instance is not in progress")
        })
        public ResponseEntity<DataResDTO<QuizStateResDTO>> getQuizState(
                        @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
                        HttpServletRequest httpServletRequest) {
                IdentityContext identity = identityResolver.resolve(httpServletRequest);
                QuizStateResDTO state = quizInstanceService.getQuizState(instanceId, identity);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(state));
        }

        @PostMapping(ApiDefined.QuizInstance.ANSWERS)
        @Operation(summary = "Save answer", description = "Save a single answer to Redis")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Answer saved successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid answer or time expired")
        })
        public ResponseEntity<DataResDTO<?>> saveAnswer(
                        @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
                        @Valid @RequestBody QuizAnswerReqDTO request,
                        HttpServletRequest httpServletRequest) {
                log.info("Saving answer for instance {}", instanceId);
                IdentityContext identity = identityResolver.resolve(httpServletRequest);
                quizInstanceService.saveAnswer(instanceId, identity, request);
                return ResponseEntity.ok(DataResDTO.ok(Map.of("success", true)));
        }

        @PostMapping(ApiDefined.QuizInstance.SUBMIT)
        @Operation(summary = "Submit quiz", description = "Submit the quiz - scores are calculated from Redis answers")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz submitted successfully"),
                        @ApiResponse(responseCode = "400", description = "Quiz instance is not in progress"),
                        @ApiResponse(responseCode = "409", description = "Quiz already submitted")
        })
        public ResponseEntity<DataResDTO<QuizResultDetailResDTO>> submitQuiz(
                        @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
                        HttpServletRequest httpServletRequest) {
                IdentityContext identity = identityResolver.resolve(httpServletRequest);
                QuizResultDetailResDTO result = quizInstanceService.submitQuiz(instanceId, identity);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.update(result));
        }

        @GetMapping(ApiDefined.QuizInstance.RESULT)
        @Operation(summary = "Get quiz result", description = "Get the result of a completed quiz instance")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz result retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Quiz instance or result not found")
        })
        public ResponseEntity<DataResDTO<QuizResultDetailResDTO>> getQuizResult(
                        @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
                        @Parameter(description = "User ID") @RequestParam(required = false) Long userId,
                        HttpServletRequest httpServletRequest) {
                IdentityContext identity = resolveIdentity(userId, httpServletRequest);
                QuizResultDetailResDTO result = quizInstanceService.getQuizResult(instanceId, identity);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(result));
        }

        @DeleteMapping(ApiDefined.QuizInstance.DELETE)
        @Operation(summary = "Delete quiz instance", description = "Delete a quiz instance")
        @PreAuthorize("hasAuthority('quiz_result_DELETE')")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz instance deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Quiz instance not found")
        })
        public ResponseEntity<DataResDTO<Object>> deleteQuizInstance(
                        @Parameter(description = "Quiz instance ID") @PathVariable Long instanceId,
                        @Parameter(description = "User ID") @RequestParam(required = false) Long userId,
                        HttpServletRequest httpServletRequest) {
                IdentityContext identity = resolveIdentity(userId, httpServletRequest);
                quizInstanceService.deleteQuizInstance(instanceId, identity);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.delete());
        }

        @GetMapping(ApiDefined.QuizInstance.CHECK_ELIGIBILITY)
        @Operation(summary = "Check user eligibility", description = "Check if a user can start a specific quiz")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Eligibility check completed successfully")
        })
        public ResponseEntity<DataResDTO<Boolean>> checkUserEligibility(
                        @Parameter(description = "Quiz ID") @RequestParam Long quizId,
                        @Parameter(description = "User ID") @RequestParam(required = false) Long userId,
                        HttpServletRequest httpServletRequest) {
                IdentityContext identity = resolveIdentity(userId, httpServletRequest);
                boolean canStart = quizInstanceService.canUserStartQuiz(quizId, identity);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(canStart));
        }
}
