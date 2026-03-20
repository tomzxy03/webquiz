package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.FolderReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.folder.FolderResDTO;
import com.tomzxy.web_quiz.services.FolderService;
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
@RequestMapping(ApiDefined.Folder.ROOT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Folder", description = "APIs for managing folders in question bank (similar to Google Drive)")
public class FolderController {
    
    private final FolderService folderService;
    
    /**
     * Create new folder
     */
    @PostMapping(ApiDefined.Folder.CREATE)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Create folder", description = "Create new folder in question bank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Folder created successfully",
                    content = @Content(schema = @Schema(implementation = FolderResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Parent folder not found")
    })
    public ResponseEntity<DataResDTO<FolderResDTO>> createFolder(
            @RequestBody FolderReqDTO dto) {
        log.info("POST /api/folders - Creating folder: {}", dto.getName());
        FolderResDTO result = folderService.createFolder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResDTO.create(result));
    }
    
    /**
     * Get folder details
     */
    @GetMapping(ApiDefined.Folder.BY_ID)
    @PreAuthorize("@questionBankSecurity.isFolderOwner(#folderId, authentication)")
    @Operation(summary = "Get folder", description = "Get folder details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FolderResDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - folder doesn't belong to you"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<DataResDTO<FolderResDTO>> getFolderById(
            @PathVariable("folderId") 
            @Parameter(description = "Folder ID", required = true) 
            Long folderId) {
        log.info("GET /api/folders/{} - Getting folder", folderId);
        FolderResDTO result = folderService.getFolderById(folderId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Get root folders (Google Drive like)
     */
    @GetMapping(ApiDefined.Folder.ROOT_FOLDERS)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Get root folders", description = "Get all root-level folders in question bank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Root folders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Question bank not found")
    })
    public ResponseEntity<DataResDTO<List<FolderResDTO>>> getRootFolders() {
        log.info("GET /api/folders/root - Getting root folders");
        List<FolderResDTO> result = folderService.getRootFolders();
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Get subfolders
     */
    @GetMapping(ApiDefined.Folder.SUBFOLDERS)
    @PreAuthorize("@questionBankSecurity.isFolderOwner(#parentFolderId, authentication)")
    @Operation(summary = "Get subfolders", description = "Get subfolders of a specific parent folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subfolders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Parent folder not found")
    })
    public ResponseEntity<DataResDTO<List<FolderResDTO>>> getSubfolders(
            @PathVariable("parentFolderId") 
            @Parameter(description = "Parent folder ID", required = true) 
            Long parentFolderId) {
        log.info("GET /api/folders/{}/subfolders - Getting subfolders", parentFolderId);
        List<FolderResDTO> result = folderService.getSubfolders(parentFolderId);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Get all folders with pagination
     */
    @GetMapping(ApiDefined.Folder.LIST)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Get all folders", description = "Get paginated list of all folders in question bank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Question bank not found")
    })
    public ResponseEntity<DataResDTO<?>> getAllFolders(
            @RequestParam(defaultValue = "0") 
            @Parameter(description = "Page number (0-based)", example = "0") 
            int page,
            @RequestParam(defaultValue = "10") 
            @Parameter(description = "Page size", example = "10") 
            int size) {
        log.info("GET /api/folders - Getting all folders - page: {}, size: {}", page, size);
        PageResDTO<?> result = folderService.getAllFolders(page, size);
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Get folder tree (nested structure)
     */
    @GetMapping(ApiDefined.Folder.TREE)
    @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
    @Operation(summary = "Get folder tree", description = "Get complete folder hierarchy with nested structure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder tree retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Question bank not found")
    })
    public ResponseEntity<DataResDTO<List<FolderResDTO>>> getFolderTree() {
        log.info("GET /api/folders/tree - Getting folder tree");
        List<FolderResDTO> result = folderService.getFolderTree();
        return ResponseEntity.ok(DataResDTO.ok(result));
    }
    
    /**
     * Update folder
     */
    @PutMapping(ApiDefined.Folder.BY_ID)
    @PreAuthorize("@questionBankSecurity.isFolderOwner(#folderId, authentication)")
    @Operation(summary = "Update folder", description = "Update folder name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder updated successfully",
                    content = @Content(schema = @Schema(implementation = FolderResDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<DataResDTO<FolderResDTO>> updateFolder(
            @PathVariable("folderId") 
            @Parameter(description = "Folder ID", required = true) 
            Long folderId,
            @RequestBody FolderReqDTO dto) {
        log.info("PUT /api/folders/{} - Updating folder", folderId);
        FolderResDTO result = folderService.updateFolder(folderId, dto);
        return ResponseEntity.ok(DataResDTO.update(result));
    }
    
    /**
     * Move folder to different parent
     */
    @PutMapping(ApiDefined.Folder.MOVE)
    @PreAuthorize("@questionBankSecurity.isFolderOwner(#folderId, authentication)")
    @Operation(summary = "Move folder", description = "Move folder to different parent folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder moved successfully",
                    content = @Content(schema = @Schema(implementation = FolderResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid move operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<DataResDTO<FolderResDTO>> moveFolder(
            @PathVariable("folderId") 
            @Parameter(description = "Folder ID to move", required = true) 
            Long folderId,
            @RequestParam 
            @Parameter(description = "New parent folder ID", required = true) 
            Long newParentFolderId) {
        log.info("PUT /api/folders/{}/move - Moving folder to parent: {}", folderId, newParentFolderId);
        FolderResDTO result = folderService.moveFolder(folderId, newParentFolderId);
        return ResponseEntity.ok(DataResDTO.update(result));
    }
    
    /**
     * Delete folder
     */
    @DeleteMapping(ApiDefined.Folder.BY_ID)
    @PreAuthorize("@questionBankSecurity.isFolderOwner(#folderId, authentication)")
    @Operation(summary = "Delete folder", description = "Delete folder and all its contents (cascade)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Folder deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<Void> deleteFolder(
            @PathVariable("folderId") 
            @Parameter(description = "Folder ID to delete", required = true) 
            Long folderId) {
        log.info("DELETE /api/folders/{} - Deleting folder", folderId);
        folderService.deleteFolder(folderId);
        return ResponseEntity.noContent().build();
    }
}
