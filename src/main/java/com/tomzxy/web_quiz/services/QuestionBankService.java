package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.QuestionBankReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.questionbank.QuestionBankResDTO;

public interface QuestionBankService {
    
    // CRUD Operations
    /**
     * Get question bank for current user
     */
    QuestionBankResDTO getMyQuestionBank();
    
    /**
     * Get question bank by owner ID
     */
    QuestionBankResDTO getQuestionBankByOwnerId(Long ownerId);
    
    /**
     * Get all question banks with pagination
     */
    PageResDTO<?> getAllQuestionBanks(int page, int size);
    
    /**
     * Create or initialize question bank for current user
     */
    QuestionBankResDTO createQuestionBank(QuestionBankReqDTO dto);
    
    /**
     * Update question bank (description, metadata)
     */
    QuestionBankResDTO updateQuestionBank(QuestionBankReqDTO dto);
    
    /**
     * Delete question bank
     */
    void deleteQuestionBank();
    
    /**
     * Get question bank by owner ID (for admin/internal use)
     */
    QuestionBankResDTO getOrCreateQuestionBank(Long userId);
}
