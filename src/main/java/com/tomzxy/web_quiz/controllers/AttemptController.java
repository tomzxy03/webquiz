package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.configs.IdentityResolver;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.AttemptDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.models.IdentityContext;
import com.tomzxy.web_quiz.services.AttemptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = ApiDefined.Attempt.BASE)
public class AttemptController {
    private final AttemptService attemptService;
    private final IdentityResolver identityResolver;

    @Operation(summary = "Get quiz attempt detail", description = "Retrieve detailed information about a specific quiz attempt (instance) by its ID. Accessible by the attempt owner or quiz host.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz attempt detail"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have access to this quiz attempt detail"),
            @ApiResponse(responseCode = "404", description = "Not Found - quiz attempt not found")
    })
    @GetMapping(ApiDefined.Attempt.ME_DETAIL)
    @PreAuthorize("@attemptSecurity.isOwner(#quizInstanceId, authentication)")
    public ResponseEntity<DataResDTO<AttemptDetailResDTO>> getResultDetail(
            @PathVariable Long quizInstanceId) {

        AttemptDetailResDTO result = attemptService.getMyAttemptDetail(quizInstanceId);

        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    @GetMapping(ApiDefined.Attempt.SUBMISSION_DETAIL)
    @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
    public ResponseEntity<DataResDTO<AttemptDetailResDTO>> getSubmissionDetail(
            @PathVariable Long groupId,
            @PathVariable Long quizId,
            @PathVariable Long quizInstanceId) {

        AttemptDetailResDTO result = attemptService.getSubmissionDetail(groupId, quizId, quizInstanceId);

        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    @Operation(summary = "Get my quiz attempts", description = "Retrieve paginated list of quiz attempts for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz attempts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have access to this resource")
    })
    @GetMapping(ApiDefined.Attempt.ME)
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getMyResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpServletRequest) {
        IdentityContext identity = identityResolver.resolve(httpServletRequest);
        PageResDTO<?> results = attemptService.getMyAttempts(identity, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(DataResDTO.ok(results));
    }
    @Operation(summary = "Get quiz attempts for a quiz", description = "Retrieve paginated list of quiz attempts for a specific quiz within a group. Accessible only by the quiz host.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz attempts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have access to this resource"),
            @ApiResponse(responseCode = "404", description = "Not Found - quiz or group not found")
    })
    @GetMapping(ApiDefined.Attempt.SUBMISSION)
    @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
    public PageResDTO<?> getQuizAttempts(
        @PathVariable Long groupId,
        @PathVariable Long quizId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    return attemptService.getQuizAttempts(quizId, groupId, page, size);
    }
}
