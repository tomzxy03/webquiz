package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.QuestionBankReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.questionbank.QuestionBankResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.Folder.QuestionBankMapper;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.QuestionBankRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuestionBankService;
import com.tomzxy.web_quiz.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankRepo questionBankRepo;
    private final UserRepo userRepo;
    private final QuestionBankMapper questionBankMapper;
    private final ConvertToPageResDTO convertToPageResDTO;

    /**
     * Get question bank for current authenticated user
     * Auto-create if doesn't exist (on first access)
     */
    @Override
    @Transactional(readOnly = false)
    public QuestionBankResDTO getMyQuestionBank() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("GET /api/question-banks/my-bank for user: {}", currentUserId);
        
        // Check if bank exists
        QuestionBank questionBank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseGet(() -> {
                    // Auto-create bank if not exists
                    log.info("QuestionBank not found for user {}, creating new one", currentUserId);
                    return createDefaultQuestionBank(currentUserId);
                });
        
        return questionBankMapper.toQuestionBankResDTO(questionBank);
    }

    /**
     * Create default QuestionBank for user if it doesn't exist
     */
    private QuestionBank createDefaultQuestionBank(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        
        QuestionBank bank = QuestionBank.builder()
                .owner(user)
                .folders(new ArrayList<>())
                .questions(new HashSet<>())
                .build();
        
        bank = questionBankRepo.save(bank);
        log.info("Created default QuestionBank for user {} (ID: {}) with bank ID: {}", 
                user.getEmail(), userId, bank.getId());
        
        return bank;
    }

    /**
     * Get question bank by owner ID
     */
    @Override
    @Transactional(readOnly = true)
    public QuestionBankResDTO getQuestionBankByOwnerId(Long ownerId) {
        log.info("Getting question bank for owner: {}", ownerId);
        
        QuestionBank questionBank = questionBankRepo.findByOwnerId(ownerId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE,
                    "Question bank not found for owner: " + ownerId));
        
        return questionBankMapper.toQuestionBankResDTO(questionBank);
    }

    /**
     * Get all question banks with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuestionBanks(int page, int size) {
        log.info("Getting all question banks - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionBank> questionBanks = questionBankRepo.findAllActive(pageable);
        
        return convertToPageResDTO.convertPageResponse(
            questionBanks, 
            pageable, 
            questionBankMapper::toQuestionBankResDTO
        );
    }

    /**
     * Create or initialize question bank for current user
     */
    @Override
    public QuestionBankResDTO createQuestionBank(QuestionBankReqDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Creating question bank for user: {}", currentUserId);
        
        // Check if already exists
        if (questionBankRepo.existsByOwnerId(currentUserId)) {
            throw new IllegalArgumentException("Question bank already exists for this user");
        }
        
        User user = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE,
                    "User not found: " + currentUserId));
        
        QuestionBank questionBank = QuestionBank.builder()
                .owner(user)
                .build();
        
        QuestionBank savedBank = questionBankRepo.save(questionBank);
        log.info("Question bank created for user: {}", currentUserId);
        
        return questionBankMapper.toQuestionBankResDTO(savedBank);
    }

    /**
     * Update question bank metadata
     */
    @Override
    public QuestionBankResDTO updateQuestionBank(QuestionBankReqDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Updating question bank for user: {}", currentUserId);
        
        QuestionBank questionBank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE,
                    "Question bank not found for user: " + currentUserId));
        
        // Update fields if needed
        // Currently no updatable fields in QuestionBank itself
        // But this can be extended for future requirements
        
        QuestionBank updated = questionBankRepo.save(questionBank);
        log.info("Question bank updated for user: {}", currentUserId);
        
        return questionBankMapper.toQuestionBankResDTO(updated);
    }

    /**
     * Delete (soft delete) question bank for current user
     */
    @Override
    public void deleteQuestionBank() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Deleting question bank for user: {}", currentUserId);
        
        QuestionBank questionBank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE,
                    "Question bank not found for user: " + currentUserId));
        
        questionBank.setActive(false);
        questionBankRepo.save(questionBank);
        log.info("Question bank deleted for user: {}", currentUserId);
    }

    /**
     * Get or create question bank for specific user (internal use)
     */
    @Override
    public QuestionBankResDTO getOrCreateQuestionBank(Long userId) {
        log.info("Getting or creating question bank for user: {}", userId);
        
        return questionBankRepo.findByOwnerId(userId)
                .map(questionBankMapper::toQuestionBankResDTO)
                .orElseGet(() -> {
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE,
                                "User not found: " + userId));
                    
                    QuestionBank questionBank = QuestionBank.builder()
                            .owner(user)
                            .build();
                    
                    QuestionBank savedBank = questionBankRepo.save(questionBank);
                    log.info("Question bank auto-created for user: {}", userId);
                    return questionBankMapper.toQuestionBankResDTO(savedBank);
                });
    }
}
