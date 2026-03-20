package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.FolderReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.folder.FolderResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.mapstructs.Folder.FolderMapper;
import com.tomzxy.web_quiz.models.Folder;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.repositories.FolderRepo;
import com.tomzxy.web_quiz.repositories.QuestionBankRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.FolderService;
import com.tomzxy.web_quiz.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@SuppressWarnings("null")
public class FolderServiceImpl implements FolderService {
    
    private final FolderRepo folderRepo;
    private final QuestionBankRepo questionBankRepo;
    private final FolderMapper folderMapper;
    private final ConvertToPageResDTO convertToPageResDTO;
    
    /**
     * Create new folder in question bank
     */
    @Override
    public FolderResDTO createFolder(FolderReqDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Creating folder '{}' for user: {}", dto.getName(), currentUserId);
        
        // Get user's question bank
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));
        
        Folder folder = folderMapper.toFolder(dto);
        folder.setBank(bank);
        
        // Set parent if provided
        if (dto.getParentFolderId() != null) {
            Folder parent = folderRepo.findById(dto.getParentFolderId())
                    .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                        "Parent folder not found: " + dto.getParentFolderId()));
            
            // Verify parent belongs to same bank
            if (!parent.getBank().getId().equals(bank.getId())) {
                throw new ApiException(AppCode.FORBIDDEN, 
                    "Parent folder doesn't belong to your question bank");
            }
            
            folder.setParent(parent);
        }
        
        Folder savedFolder = folderRepo.save(folder);
        log.info("Folder created with ID: {}", savedFolder.getId());
        
        return folderMapper.toFolderResDTO(savedFolder);
    }
    
    /**
     * Get folder details
     */
    @Override
    @Transactional(readOnly = true)
    public FolderResDTO getFolderById(Long folderId) {
        log.info("Getting folder: {}", folderId);
        
        Folder folder = folderRepo.findByIdWithDetails(folderId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Folder not found: " + folderId));
        
        // Verify ownership
        if (!isFolderOwner(folderId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to access this folder");
        }
        
        return folderMapper.toFolderResDTO(folder);
    }
    
    /**
     * Get root folders in user's question bank
     */
    @Override
    @Transactional(readOnly = true)
    public List<FolderResDTO> getRootFolders() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting root folders for user: {}", currentUserId);
        
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));
        
        // OPTIMIZATION: Use batch query to avoid N+1 (FETCH JOIN children)
        List<Folder> folders = folderRepo.findRootFoldersWithChildren(bank.getId());
        return folders.stream()
                .map(folderMapper::toFolderResDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get subfolders of a specific folder
     */
    @Override
    @Transactional(readOnly = true)
    public List<FolderResDTO> getSubfolders(Long parentFolderId) {
        log.info("Getting subfolders for parent: {}", parentFolderId);
        
        // Verify parent ownership
        if (!isFolderOwner(parentFolderId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to access this folder");
        }
        
        List<Folder> subfolders = folderRepo.findByParentId(parentFolderId);
        return subfolders.stream()
                .map(folderMapper::toFolderResDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all folders in question bank with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllFolders(int page, int size) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting all folders for user: {}, page: {}, size: {}", currentUserId, page, size);
        
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Folder> folders = folderRepo.findByBankId(bank.getId(), pageable);
        
        return convertToPageResDTO.convertPageResponse(
            folders,
            pageable,
            folderMapper::toFolderResDTO
        );
    }
    
    /**
     * Get folder tree structure (with nested children)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FolderResDTO> getFolderTree() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting folder tree for user: {}", currentUserId);
        
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));
        
        // Get all root folders
        List<Folder> rootFolders = folderRepo.findRootFoldersByBankId(bank.getId());
        
        // Build tree structure
        return rootFolders.stream()
                .map(this::buildFolderTree)
                .collect(Collectors.toList());
    }
    
    /**
     * Build folder tree recursively
     */
    private FolderResDTO buildFolderTree(Folder folder) {
        FolderResDTO dto = folderMapper.toFolderResDTO(folder);
        
        // Add children recursively
        if (folder.getChildren() != null && !folder.getChildren().isEmpty()) {
            List<FolderResDTO> childrenDTOs = folder.getChildren().stream()
                    .map(this::buildFolderTree)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDTOs);
        }
        
        return dto;
    }
    
    /**
     * Update folder name
     */
    @Override
    public FolderResDTO updateFolder(Long folderId, FolderReqDTO dto) {
        log.info("Updating folder: {}", folderId);
        
        // Verify ownership
        if (!isFolderOwner(folderId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to update this folder");
        }
        
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Folder not found: " + folderId));
        
        // Update name
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            folder.setName(dto.getName());
        }
        
        Folder updated = folderRepo.save(folder);
        log.info("Folder updated: {}", folderId);
        
        return folderMapper.toFolderResDTO(updated);
    }
    
    /**
     * Move folder to different parent
     */
    @Override
    public FolderResDTO moveFolder(Long folderId, Long newParentFolderId) {
        log.info("Moving folder {} to new parent: {}", folderId, newParentFolderId);
        
        // Verify ownership of source folder
        if (!isFolderOwner(folderId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to move this folder");
        }
        
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Folder not found: " + folderId));
        
        // Prevent circular reference
        if (folderId.equals(newParentFolderId)) {
            throw new ApiException(AppCode.BAD_REQUEST, 
                "Cannot move folder to itself");
        }
        
        // OPTIMIZATION: Use native recursive query to check circular reference
        if (folderRepo.isDescendantOf(folderId, newParentFolderId)) {
            throw new ApiException(AppCode.BAD_REQUEST, 
                "Cannot move folder to its descendant");
        }
        
        // Verify new parent belongs to same bank
        Folder newParent = folderRepo.findById(newParentFolderId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "New parent folder not found: " + newParentFolderId));
        
        if (!newParent.getBank().getId().equals(folder.getBank().getId())) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "New parent folder doesn't belong to your question bank");
        }
        
        folder.setParent(newParent);
        Folder updated = folderRepo.save(folder);
        log.info("Folder moved: {}", folderId);
        
        return folderMapper.toFolderResDTO(updated);
    }
    
    /**
     * Delete folder (and all subfolders/questions)
     */
    @Override
    public void deleteFolder(Long folderId) {
        log.info("Deleting folder: {}", folderId);
        
        // Verify ownership
        if (!isFolderOwner(folderId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to delete this folder");
        }
        
        // OPTIMIZATION: Use soft delete to avoid N+1 cascade queries
        folderRepo.deactivateByIdAndChildren(folderId);
        log.info("Folder soft-deleted: {}", folderId);
    }
    
    /**
     * Check if folder belongs to current user's question bank
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isFolderOwner(Long folderId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // OPTIMIZATION: Use query-level ownership check to avoid loading folder + bank + user
        return folderRepo.existsByIdAndOwnerId(folderId, currentUserId);
    }
}
