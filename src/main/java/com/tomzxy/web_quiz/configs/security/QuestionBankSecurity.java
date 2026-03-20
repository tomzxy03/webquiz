package com.tomzxy.web_quiz.configs.security;

import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.QuestionBankRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Security component for Question Bank authorization
 * Each user has 1 question bank (private)
 * Pattern: @PreAuthorize("@questionBankSecurity.isOwner(authentication)")
 */
@Component("questionBankSecurity")
@RequiredArgsConstructor
@Slf4j
public class QuestionBankSecurity {

    private final QuestionBankRepo questionBankRepo;
    private final UserRepo userRepo;

    /**
     * Check if current user owns the question bank
     * - Each user has only 1 bank
     * - Used for CREATE, UPDATE, DELETE operations on own bank
     */
    public boolean isOwner(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found: " + email));

        boolean isOwner = questionBankRepo.existsByOwnerId(user.getId());
        if (isOwner == false) {
            log.debug("User has no QuestionBank yet, allow access to create one");
            return true; // cho phép truy cập để tạo bank
        }
        
        return isOwner;
    }

    /**
     * Check if current user can access/view question bank
     * Same as isOwner for private banks (each user has own bank)
     */
    public boolean canAccess(Authentication authentication) {
        return isOwner(authentication);
    }

    /**
     * Check if current user owns a specific folder
     * Folder must belong to current user's bank
     */
    public boolean isFolderOwner(Long folderId, Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found: " + email));

        boolean isOwner = questionBankRepo.existsByIdAndOwnerId(folderId, user.getId());
        
        log.debug("Check folder owner for user: {} (ID: {}) folder: {} => {}", 
            email, user.getId(), folderId, isOwner);
        
        return isOwner;
    }

    /**
     * Check if current user owns a specific question
     * Question must belong to current user's bank
     */
    public boolean isQuestionOwner(Long questionId, Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found: " + email));

        boolean isOwner = questionBankRepo.existsByQuestionIdAndOwnerId(questionId, user.getId());
        
        log.debug("Check question owner for user: {} (ID: {}) question: {} => {}", 
            email, user.getId(), questionId, isOwner);
        
        return isOwner;
    }
}
