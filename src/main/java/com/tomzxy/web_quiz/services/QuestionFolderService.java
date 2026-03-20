package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.QuestionFolderReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionFolderResDTO;

import java.util.List;

public interface QuestionFolderService {
    
    /**
     * Create question in a folder
     */
    QuestionFolderResDTO createQuestionInFolder(QuestionFolderReqDTO dto);
    
    /**
     * Get questions in a specific folder
     */
    List<QuestionFolderResDTO> getQuestionsInFolder(Long folderId);
    
    /**
     * Get root-level questions (without folder)
     */
    List<QuestionFolderResDTO> getRootLevelQuestions();
    
    /**
     * Get all questions in bank with pagination
     */
    PageResDTO<?> getAllQuestionsByBank(int page, int size);
    
    /**
     * Move question to folder
     */
    QuestionFolderResDTO moveQuestionToFolder(Long questionId, Long folderId);
    
    /**
     * Move question to root level (remove from folder)
     */
    QuestionFolderResDTO moveQuestionToRoot(Long questionId);
    
    /**
     * Get questions with folder tree structure
     */
    List<Object> getQuestionTreeStructure();
    
    /**
     * Delete question
     */
    void deleteQuestion(Long questionId);
    
    /**
     * Check if question belongs to current user's bank
     */
    boolean isQuestionOwner(Long questionId);
}
