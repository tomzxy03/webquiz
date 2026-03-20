package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.QuestionFolderReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionFolderResDTO;
import com.tomzxy.web_quiz.services.QuestionFolderService;
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

import java.util.List;

@RestController
@RequestMapping(ApiDefined.QuestionFolder.ROOT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Folder", description = "APIs for managing questions in folders (Google Drive like structure)")
public class QuestionFolderController {
    
    private final QuestionFolderService questionFolderService;
    
    /**
     * Create question in folder (Private - owner only)
     */
    @PostMapping(ApiDefined.QuestionFolder.CREATE)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Create question in folder", description = "Create new question in specific folder or root level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question created successfully",
                    content = @Content(schema = @Schema(implementation = QuestionFolderResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Folder or bank not found")
    })
    public ResponseEntity<DataResDTO<QuestionFolderResDTO>> createQuestionInFolder(
            @RequestBody QuestionFolderReqDTO dto) {
        log.info("POST /api/questions-folder - Creating questions: {}", dto.getQuestions());
        QuestionFolderResDTO result = questionFolderService.createQuestionInFolder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResDTO.create(result));
    }
    
    /**
     * Get questions in folder (Private - owner only)
     */
    @GetMapping(ApiDefined.QuestionFolder.BY_FOLDER)
    @PreAuthorize("@questionBankSecurity.isFolderOwner(#folderId, authentication)")
    @Operation(summary = "Get questions in folder", description = "Get all questions in specific folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<DataResDTO<List<QuestionFolderResDTO>>> getQuestionsInFolder(
            @PathVariable("folderId") 
            @Parameter(description = "Folder ID", required = true) 
            Long folderId) {
        log.info("GET /api/questions-folder/folder/{} - Getting questions in folder", folderId);
        List<QuestionFolderResDTO> result = questionFolderService.getQuestionsInFolder(folderId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Get root-level questions (Private - owner only)
     */
    @GetMapping(ApiDefined.QuestionFolder.ROOT_LEVEL)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Get root-level questions", description = "Get questions at root level (without folder)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Question bank not found")
    })
    public ResponseEntity<DataResDTO<List<QuestionFolderResDTO>>> getRootLevelQuestions() {
        log.info("GET /api/questions-folder/root - Getting root-level questions");
        List<QuestionFolderResDTO> result = questionFolderService.getRootLevelQuestions();
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Get all questions with pagination (Private - owner only)
     */
    @GetMapping(ApiDefined.QuestionFolder.LIST)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Get all questions", description = "Get paginated list of all questions in bank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Question bank not found")
    })
    public ResponseEntity<DataResDTO<?>> getAllQuestionsByBank(
            @RequestParam(defaultValue = "0") 
            @Parameter(description = "Page number (0-based)", example = "0") 
            int page,
            @RequestParam(defaultValue = "10") 
            @Parameter(description = "Page size", example = "10") 
            int size) {
        log.info("GET /api/questions-folder - Getting all questions - page: {}, size: {}", page, size);
        PageResDTO<?> result = questionFolderService.getAllQuestionsByBank(page, size);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Move question to folder (Private - owner only)
     */
    @PutMapping(ApiDefined.QuestionFolder.MOVE_TO_FOLDER)
    @PreAuthorize("@questionBankSecurity.isQuestionOwner(#questionId, authentication)")
    @Operation(summary = "Move question to folder", description = "Move question to different folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question moved successfully",
                    content = @Content(schema = @Schema(implementation = QuestionFolderResDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Question or folder not found")
    })
    public ResponseEntity<DataResDTO<QuestionFolderResDTO>> moveQuestionToFolder(
            @PathVariable("questionId") 
            @Parameter(description = "Question ID", required = true) 
            Long questionId,
            @RequestParam 
            @Parameter(description = "Target folder ID", required = true) 
            Long folderId) {
        log.info("PUT /api/questions-folder/{}/move - Moving question to folder: {}", questionId, folderId);
        QuestionFolderResDTO result = questionFolderService.moveQuestionToFolder(questionId, folderId);
        return ResponseEntity.ok(DataResDTO.update(result));
    }
    
    /**
     * Move question to root level (Private - owner only)
     */
    @PutMapping(ApiDefined.QuestionFolder.MOVE_TO_ROOT)
    @PreAuthorize("@questionBankSecurity.isQuestionOwner(#questionId, authentication)")
    @Operation(summary = "Move question to root", description = "Move question to root level (remove from folder)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question moved successfully",
                    content = @Content(schema = @Schema(implementation = QuestionFolderResDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<DataResDTO<QuestionFolderResDTO>> moveQuestionToRoot(
            @PathVariable("questionId") 
            @Parameter(description = "Question ID", required = true) 
            Long questionId) {
        log.info("PUT /api/questions-folder/{}/move-root - Moving question to root", questionId);
        QuestionFolderResDTO result = questionFolderService.moveQuestionToRoot(questionId);
        return ResponseEntity.ok(DataResDTO.update(result));
    }
    
    /**
     * Get question tree structure (Private - owner only)
     */
    @GetMapping(ApiDefined.QuestionFolder.TREE)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Get question tree", description = "Get complete question hierarchy organized by folders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question tree retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Question bank not found")
    })
    public ResponseEntity<DataResDTO<List<Object>>> getQuestionTreeStructure() {
        log.info("GET /api/questions-folder/tree - Getting question tree");
        List<Object> result = questionFolderService.getQuestionTreeStructure();
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Delete question (Private - owner only)
     */
    @DeleteMapping(ApiDefined.QuestionFolder.BY_ID)
    @PreAuthorize("@questionBankSecurity.isQuestionOwner(#questionId, authentication)")
    @Operation(summary = "Delete question", description = "Delete question permanently")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable("questionId") 
            @Parameter(description = "Question ID to delete", required = true) 
            Long questionId) {
        log.info("DELETE /api/questions-folder/{} - Deleting question", questionId);
        questionFolderService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
