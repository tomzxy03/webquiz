package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.QuizMapper;
import com.tomzxy.web_quiz.models.Quiz;
import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.repositories.LobbyRepo;
import com.tomzxy.web_quiz.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LobbyRepo lobbyRepo;

    @Autowired
    private QuizMapper quizMapper;

    @Override
    @Transactional(readOnly = true)
    public DataResDTO<PageResDTO<java.util.List<QuizResDTO>>> getAll(Pageable pageable) {
        try {
            Page<Quiz> page = quizRepo.findAll(pageable);
            PageResDTO<java.util.List<QuizResDTO>> pageResDTO = PageResDTO.<java.util.List<QuizResDTO>>builder()
                    .page(page.getNumber())
                    .size(page.getSize())
                    .total_page(page.getTotalPages())
                    .items(page.getContent().stream().map(this::mapToDtoWithRelations).collect(Collectors.toList()))
                    .build();
            return DataResDTO.ok(pageResDTO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve quizzes: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DataResDTO<QuizResDTO> getById(Long id) {
        try {
            Quiz quiz = quizRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));
            return DataResDTO.ok(mapToDtoWithRelations(quiz));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve quiz: " + e.getMessage());
        }
    }

    @Override
    public DataResDTO<QuizResDTO> create(QuizReqDTO dto) {
        try {
            // Validate host exists
            User host = userRepo.findById(dto.getHostId())
                    .orElseThrow(() -> new NotFoundException("Host user not found with id: " + dto.getHostId()));

            // Validate group if provided
            Lobby group = null;
            if (dto.getGroupId() != null) {
                group = lobbyRepo.findById(dto.getGroupId())
                        .orElseThrow(() -> new NotFoundException("Group not found with id: " + dto.getGroupId()));
                
                // Validate host is member of the group
                if (!group.getMembers().contains(host)) {
                    throw new RuntimeException("Host must be a member of the specified group");
                }
            }

            // Create quiz
            Quiz quiz = quizMapper.toEntity(dto);
            quiz.setHost(host);
            quiz.setGroup(group);
            
            // Validate total questions match
            if (dto.getQuestions() != null && dto.getQuestions().size() != dto.getTotalQuestion()) {
                throw new RuntimeException("Total questions count must match the number of questions provided");
            }

            quiz = quizRepo.save(quiz);
            return DataResDTO.create(mapToDtoWithRelations(quiz));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create quiz: " + e.getMessage());
        }
    }

    @Override
    public DataResDTO<QuizResDTO> update(Long id, QuizReqDTO dto) {
        try {
            Quiz existingQuiz = quizRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));

            // Validate host exists
            User host = userRepo.findById(dto.getHostId())
                    .orElseThrow(() -> new NotFoundException("Host user not found with id: " + dto.getHostId()));

            // Validate group if provided
            Lobby group = null;
            if (dto.getGroupId() != null) {
                group = lobbyRepo.findById(dto.getGroupId())
                        .orElseThrow(() -> new NotFoundException("Group not found with id: " + dto.getGroupId()));
                
                // Validate host is member of the group
                if (!group.getMembers().contains(host)) {
                    throw new RuntimeException("Host must be a member of the specified group");
                }
            }

            // Update quiz
            Quiz updatedQuiz = quizMapper.toEntity(dto);
            updatedQuiz.setId(existingQuiz.getId());
            updatedQuiz.setHost(host);
            updatedQuiz.setGroup(group);
            
            // Validate total questions match
            if (dto.getQuestions() != null && dto.getQuestions().size() != dto.getTotalQuestion()) {
                throw new RuntimeException("Total questions count must match the number of questions provided");
            }

            updatedQuiz = quizRepo.save(updatedQuiz);
            return DataResDTO.update(mapToDtoWithRelations(updatedQuiz));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update quiz: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Quiz quiz = quizRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));
            quizRepo.delete(quiz);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete quiz: " + e.getMessage());
        }
    }

    /**
     * Helper method to map Quiz to DTO with proper relationship handling
     */
    private QuizResDTO mapToDtoWithRelations(Quiz quiz) {
        QuizResDTO dto = quizMapper.toDto(quiz);
        // TODO: Add proper mapping for questions and submissions when their mappers are available
        return dto;
    }
} 