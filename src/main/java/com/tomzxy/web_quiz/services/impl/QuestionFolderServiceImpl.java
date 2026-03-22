package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.QuestionFolderReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.folder.FolderQuestionsTreeDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionFolderResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
import com.tomzxy.web_quiz.models.Folder;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.repositories.FolderRepo;
import com.tomzxy.web_quiz.repositories.QuestionBankRepo;
import com.tomzxy.web_quiz.repositories.QuestionRepo;
import com.tomzxy.web_quiz.services.QuestionFolderService;
import com.tomzxy.web_quiz.services.QuestionService;
import com.tomzxy.web_quiz.services.common.ConvertToPageResDTO;
import com.tomzxy.web_quiz.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionFolderServiceImpl implements QuestionFolderService {
    
    private final QuestionRepo questionRepo;
    private final QuestionBankRepo questionBankRepo;
    private final FolderRepo folderRepo;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final QuestionService  questionService;
    private final QuestionMapper questionMapper;
    
    /**
     * Create question in a folder
     */
    @Override
    public QuestionFolderResDTO createQuestionInFolder(QuestionFolderReqDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Creating question '{}' in bank for user: {}", dto.getQuestions(), currentUserId);
        
        // Get user's question bank
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));

        Question question = questionService.create_Questions(dto.getQuestions());
        question.setBank(bank);
        // Set folder if provided
        if (dto.getFolderId() != null) {
            Folder folder = folderRepo.findById(dto.getFolderId())
                    .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                        "Folder not found: " + dto.getFolderId()));
            
            // Verify folder belongs to same bank
            if (!folder.getBank().getId().equals(bank.getId())) {
                throw new ApiException(AppCode.FORBIDDEN, 
                    "Folder doesn't belong to your question bank");
            }
            
            question.setFolder(folder);
        }
        
        Question saved = questionRepo.save(question);
        log.info("Question created with ID: {}", saved.getId());
        
        return mapToQuestionFolderResDTO(saved);
    }
    
    /**
     * Get questions in a specific folder
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionFolderResDTO> getQuestionsInFolder(Long folderId) {
        log.info("Getting questions in folder: {}", folderId);
        
        // Verify folder ownership
        if (!isFolderOwner(folderId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to access this folder");
        }
        
        // OPTIMIZATION: Use eager-loaded folder with questions
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Folder not found: " + folderId));
        List<Question> questions = questionService.getQuestionByFolderId(folderId);
        // get log folder id and bank id in questions
        
        return questions.stream()
                .map(this::mapToQuestionFolderResDTO)
                .toList();
    }
    
    /**
     * Get root-level questions (without folder)
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionFolderResDTO> getRootLevelQuestions() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting root-level questions for user: {}", currentUserId);
        
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));
        
        return bank.getQuestions().stream()
                .filter(q -> q.getFolder() == null)
                .map(this::mapToQuestionFolderResDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all questions in bank with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuestionsByBank(int page, int size) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting all questions for user: {}, page: {}, size: {}", currentUserId, page, size);
        
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions = questionRepo.findByBankId(bank.getId(), pageable);
        
        return convertToPageResDTO.convertPageResponse(
            questions,
            pageable,
            this::mapToQuestionFolderResDTO
        );
    }
    
    /**
     * Move question to folder
     */
    @Override
    public QuestionFolderResDTO moveQuestionToFolder(Long questionId, Long folderId) {
        log.info("Moving question {} to folder: {}", questionId, folderId);
        
        // Verify ownership of question
        if (!isQuestionOwner(questionId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to move this question");
        }
        
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question not found: " + questionId));
        
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Folder not found: " + folderId));
        
        // Verify folder belongs to same bank
        if (!folder.getBank().getId().equals(question.getBank().getId())) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "Folder doesn't belong to the same question bank");
        }
        
        question.setFolder(folder);
        Question updated = questionRepo.save(question);
        log.info("Question moved to folder: {}", folderId);
        
        return mapToQuestionFolderResDTO(updated);
    }
    
    /**
     * Move question to root level (remove from folder)
     */
    @Override
    public QuestionFolderResDTO moveQuestionToRoot(Long questionId) {
        log.info("Moving question {} to root level", questionId);
        
        // Verify ownership
        if (!isQuestionOwner(questionId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to move this question");
        }
        
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question not found: " + questionId));
        
        question.setFolder(null);
        Question updated = questionRepo.save(question);
        log.info("Question moved to root level: {}", questionId);
        
        return mapToQuestionFolderResDTO(updated);
    }
    
    /**
     * Get questions with folder tree structure
     */
    @Override
    @Transactional(readOnly = true)
    public List<Object> getQuestionTreeStructure() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting question tree for user: {}", currentUserId);
        
        QuestionBank bank = questionBankRepo.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question bank not found for user: " + currentUserId));
        
        List<Object> result = new ArrayList<>();
        
        // Add root-level questions
        List<QuestionFolderResDTO> rootQuestions = getRootLevelQuestions();
        if (!rootQuestions.isEmpty()) {
            result.add(rootQuestions);
        }
        
        // Add folders with their questions
        List<Folder> rootFolders = folderRepo.findRootFoldersByBankId(bank.getId());
        rootFolders.forEach(folder -> {
            result.add(buildFolderQuestionsTree(folder));
        });
        
        return result;
    }
    
    /**
     * Build folder tree with questions recursively
     */
    private FolderQuestionsTreeDTO buildFolderQuestionsTree(Folder folder) {
        List<FolderQuestionsTreeDTO> subfolderDTOs = folder.getChildren().stream()
                .map(this::buildFolderQuestionsTree)
                .collect(Collectors.toList());
        
        return FolderQuestionsTreeDTO.builder()
                .folderId(folder.getId())
                .folderName(folder.getName())
                .questions(folder.getQuestions().stream()
                        .map(this::mapToQuestionFolderResDTO)
                        .collect(Collectors.toList()))
                .subfolders(subfolderDTOs)
                .build();
    }
    
    /**
     * Delete question
     */
    @Override
    public void deleteQuestion(Long questionId) {
        log.info("Deleting question: {}", questionId);
        
        // Verify ownership
        if (!isQuestionOwner(questionId)) {
            throw new ApiException(AppCode.FORBIDDEN, 
                "You don't have permission to delete this question");
        }
        
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ApiException(AppCode.NOT_AVAILABLE, 
                    "Question not found: " + questionId));
        
        // OPTIMIZATION: Soft delete instead of hard delete to avoid cascade issues
        question.setActive(false);
        questionRepo.save(question);
        log.info("Question soft-deleted: {}", questionId);
    }
    
    /**
     * Check if question belongs to current user's bank
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isQuestionOwner(Long questionId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // OPTIMIZATION: Query-level ownership check
        return questionRepo.existsByIdAndOwnerId(questionId, currentUserId);
    }
    
    /**
     * Map Question to QuestionFolderResDTO
     */
    private QuestionFolderResDTO mapToQuestionFolderResDTO(Question question) {
        return QuestionFolderResDTO.builder()
                .id(question.getId())
                .question(questionMapper.toQuestionResDTO(question))
                .bankId(question.getBank().getId())
                .folderId(question.getFolder() != null ? question.getFolder().getId() : null)
                .folderName(question.getFolder() != null ? question.getFolder().getName() : null)
                .answerCount(question.getAnswers() != null ? question.getAnswers().size() : 0)
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
    
    /**
     * Helper method: Check if folder belongs to current user
     */
    private boolean isFolderOwner(Long folderId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // OPTIMIZATION: Query-level ownership check
        return folderRepo.existsByIdAndOwnerId(folderId, currentUserId);
    }
}
