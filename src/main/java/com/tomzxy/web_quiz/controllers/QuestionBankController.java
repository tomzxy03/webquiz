package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.QuestionBankReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.questionbank.QuestionBankResDTO;
import com.tomzxy.web_quiz.services.QuestionBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiDefined.QuestionBank.ROOT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Bank", description = "APIs for managing question banks")
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    /**
     * Get current user's question bank (Private - owner only)
     */
    @GetMapping(ApiDefined.QuestionBank.MY_BANK)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Get my question bank", description = "Retrieve the question bank of the current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question bank retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QuestionBankResDTO.class))),
            @ApiResponse(responseCode = "404", description = "Question bank not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DataResDTO<QuestionBankResDTO>> getMyQuestionBank() {
        log.info("GET /api/question-banks/my-bank");
        QuestionBankResDTO result = questionBankService.getMyQuestionBank();
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    /**
     * Get question bank by owner ID (Public - requires permission)
     * NOTE: This is read-only access to other users' banks - consider marking as admin-only
     */
    @GetMapping(ApiDefined.QuestionBank.BY_OWNER_ID)
    @PreAuthorize("hasAuthority('question_bank_VIEW')")
    @Operation(summary = "Get question bank by owner", description = "Retrieve question bank for a specific owner ID (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question bank retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QuestionBankResDTO.class))),
            @ApiResponse(responseCode = "404", description = "Question bank not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DataResDTO<QuestionBankResDTO>> getQuestionBankByOwnerId(
            @PathVariable("ownerId") 
            @Parameter(description = "Owner user ID", required = true) 
            Long ownerId) {
        log.info("GET /api/question-banks/{} - Getting question bank for owner", ownerId);
        QuestionBankResDTO result = questionBankService.getQuestionBankByOwnerId(ownerId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    /**
     * Get all question banks with pagination (Admin only)
     */
    @GetMapping(ApiDefined.QuestionBank.LIST)
    @PreAuthorize("hasAuthority('question_bank_VIEW')")
    @Operation(summary = "Get all question banks", description = "Retrieve paginated list of all question banks (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question banks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DataResDTO<?>> getAllQuestionBanks(
            @RequestParam(defaultValue = "0") 
            @Parameter(description = "Page number (0-based)", example = "0") 
            int page,
            @RequestParam(defaultValue = "10") 
            @Parameter(description = "Page size", example = "10") 
            int size) {
        log.info("GET /api/question-banks - Getting all question banks - page: {}, size: {}", page, size);
        PageResDTO<?> result = questionBankService.getAllQuestionBanks(page, size);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }

    /**
     * Create question bank for current user (Private - owner only)
     */
    @PostMapping(ApiDefined.QuestionBank.CREATE)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Create question bank", description = "Create or initialize a new question bank for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question bank created successfully",
                    content = @Content(schema = @Schema(implementation = QuestionBankResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or question bank already exists"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DataResDTO<QuestionBankResDTO>> createQuestionBank(
            @RequestBody 
            @Parameter(description = "Question bank creation request") 
            QuestionBankReqDTO dto) {
        log.info("POST /api/question-banks - Creating new question bank");
        QuestionBankResDTO result = questionBankService.createQuestionBank(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResDTO.create(result));
    }

    /**
     * Update question bank for current user (Private - owner only)
     */
    @PutMapping(ApiDefined.QuestionBank.UPDATE)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Update question bank", description = "Update question bank metadata for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question bank updated successfully",
                    content = @Content(schema = @Schema(implementation = QuestionBankResDTO.class))),
            @ApiResponse(responseCode = "404", description = "Question bank not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DataResDTO<QuestionBankResDTO>> updateQuestionBank(
            @RequestBody 
            @Parameter(description = "Question bank update request") 
            QuestionBankReqDTO dto) {
        log.info("PUT /api/question-banks - Updating question bank");
        QuestionBankResDTO result = questionBankService.updateQuestionBank(dto);
        return ResponseEntity.ok(DataResDTO.update(result));
    }

    /**
     * Delete (soft delete) question bank for current user (Private - owner only)
     */
    @DeleteMapping(ApiDefined.QuestionBank.DELETE)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Delete question bank", description = "Soft delete the question bank for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question bank deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question bank not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteQuestionBank() {
        log.info("DELETE /api/question-banks - Deleting question bank");
        questionBankService.deleteQuestionBank();
        return ResponseEntity.noContent().build();
    }
}
