package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.FolderReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.folder.FolderResDTO;

import java.util.List;

public interface FolderService {
    
    /**
     * Create new folder in question bank
     */
    FolderResDTO createFolder(FolderReqDTO dto);
    
    /**
     * Get folder details
     */
    FolderResDTO getFolderById(Long folderId);
    
    /**
     * Get root folders in user's question bank
     */
    List<FolderResDTO> getRootFolders();
    
    /**
     * Get subfolders of a specific folder
     */
    List<FolderResDTO> getSubfolders(Long parentFolderId);
    
    /**
     * Get all folders in question bank with pagination
     */
    PageResDTO<?> getAllFolders(int page, int size);
    
    /**
     * Get folder tree structure (with nested children)
     */
    List<FolderResDTO> getFolderTree();
    
    /**
     * Update folder name
     */
    FolderResDTO updateFolder(Long folderId, FolderReqDTO dto);
    
    /**
     * Move folder to different parent
     */
    FolderResDTO moveFolder(Long folderId, Long newParentFolderId);
    
    /**
     * Delete folder (and all subfolders/questions)
     */
    void deleteFolder(Long folderId);
    
    /**
     * Check if folder belongs to current user's question bank
     */
    boolean isFolderOwner(Long folderId);
}
