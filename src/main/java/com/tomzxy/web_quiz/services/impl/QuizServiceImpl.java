package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.QuizQuestionReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.requests.QuizAnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.QuizMapper;
import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizAttempt;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.QuizQuestion;
import com.tomzxy.web_quiz.models.QuizAnswer;
import com.tomzxy.web_quiz.models.QuizResult;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.repositories.LobbyRepo;
import com.tomzxy.web_quiz.repositories.QuestionRepo;
import com.tomzxy.web_quiz.repositories.AnswerRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {
    private final QuizRepo quizRepo;

    private final UserRepo userRepo;

    private final LobbyRepo lobbyRepo;

    private final QuestionRepo questionRepo;

    private final AnswerRepo answerRepo;

    private final QuizMapper quizMapper;

    private final ConvertToPageResDTO convertToPageResDTO;

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAll(int page,int size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<Quiz> quizzes = quizRepo.findAll(pageable);
            return convertToPageResDTO.convertPageResponse(quizzes, pageable,
            quizMapper::toDto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve quizzes: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResDTO getById(Long id) {
        try {
            Quiz quiz = quizRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));
            return quizMapper.toDto(quiz);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve quiz: " + e.getMessage());
        }
    }

    @Override
    public QuizResDTO create(QuizReqDTO dto) {
        try {
            // Validate host exists
            User host = userRepo.findById(dto.getHostId())
                    .orElseThrow(() -> new NotFoundException("Host user not found with id: " + dto.getHostId()));

            // Validate group if provided
            Lobby group = null;
            if (dto.getLobbyId() != null) {
                group = lobbyRepo.findById(dto.getLobbyId())
                        .orElseThrow(() -> new NotFoundException("Group not found with id: " + dto.getLobbyId()));
            }

            // Create quiz
            Quiz quiz = quizMapper.toEntity(dto);
            quiz.setHost(host);
            quiz.setLobby(group);
            
            // Validate total questions match
            if (dto.getQuestions() != null && dto.getQuestions().size() != dto.getTotalQuestions()) {
                throw new RuntimeException("Total questions count must match the number of questions provided");
            }

            // Process questions and answers
            if (dto.getQuestions() != null) {
                Set<QuizQuestion> quizQuestions = processQuizQuestions(dto.getQuestions());
                quiz.addQuestions(quizQuestions);
            }

            quiz = quizRepo.save(quiz);
            return mapToDtoWithRelations(quiz);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create quiz: " + e.getMessage());
        }
    }

    @Override
    public QuizResDTO update(Long id, QuizReqDTO dto) {
        try {
            Quiz existingQuiz = quizRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));

            // Validate host exists
            User host = userRepo.findById(dto.getHostId())
                    .orElseThrow(() -> new NotFoundException("Host user not found with id: " + dto.getHostId()));

            // Validate group if provided
            Lobby group = null;
            if (dto.getLobbyId() != null) {
                group = lobbyRepo.findById(dto.getLobbyId())
                        .orElseThrow(() -> new NotFoundException("Group not found with id: " + dto.getLobbyId()));
                
                // Validate host is member of the group
                if (!group.getMembers().contains(host)) {
                    throw new RuntimeException("Host must be a member of the specified group");
                }
            }

            // Update quiz
            Quiz updatedQuiz = quizMapper.toEntity(dto);
            updatedQuiz.setId(existingQuiz.getId());
            updatedQuiz.setHost(host);
            updatedQuiz.setLobby(group);
            
            // Validate total questions match
            if (dto.getQuestions() != null && dto.getQuestions().size() != dto.getTotalQuestions()) {
                throw new RuntimeException("Total questions count must match the number of questions provided");
            }

            // Process questions and answers
            if (dto.getQuestions() != null) {
                Set<QuizQuestion> quizQuestions = processQuizQuestions(dto.getQuestions());
                updatedQuiz.addQuestions(quizQuestions);
            }

            updatedQuiz = quizRepo.save(updatedQuiz);
            return mapToDtoWithRelations(updatedQuiz);
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
     * Process quiz questions and answers, handling existing vs custom content
     */
    private Set<QuizQuestion> processQuizQuestions(Set<QuizQuestionReqDTO> questionDtos) {
        return questionDtos.stream()
                .map(this::processQuizQuestion)
                .collect(Collectors.toSet());
    }

    /**
     * Process a single quiz question, creating QuizQuestion with proper relationships
     */
    @Transactional
    private QuizQuestion processQuizQuestion(QuizQuestionReqDTO questionDto) {
        QuizQuestion quizQuestion = new QuizQuestion();
        if (questionDto.getIsCustom() == true) {
            quizQuestion.setCustomQuestion(questionDto.getCustomQuestion());
        } else {
            Question existingQuestion = questionRepo.findById(questionDto.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Question not found with id: " + questionDto.getQuestionId()));
            quizQuestion.setQuestion(existingQuestion);
        }
        quizQuestion.setIsCustom(questionDto.getIsCustom());

        if (questionDto.getIsCustom()) {
            // Custom question - set custom question text
            quizQuestion.setCustomQuestion(questionDto.getCustomQuestion());
            quizQuestion.setQuestion(null); // No existing question reference
        } else {
            // Existing question - validate and set reference
            if (questionDto.getQuestionId() == null) {
                throw new RuntimeException("Question ID is required when isCustom is false");
            }
            
            Question existingQuestion = questionRepo.findById(questionDto.getQuestionId())
                    .orElseThrow(() -> new NotFoundException("Question not found with id: " + questionDto.getQuestionId()));
            
            quizQuestion.setQuestion(existingQuestion);
            quizQuestion.setCustomQuestion(null);
        }

        // Process answers for this question
        if (questionDto.getQuizAnswers() != null) {
            Set<QuizAnswer> quizAnswers = processQuizAnswers(questionDto.getQuizAnswers());
            quizQuestion.saveAnswers(quizAnswers);
        }

        return quizQuestion;
    }

    /**
     * Create a QuizAttempt with complete snapshot of question and answers
     */
    private QuizAttempt createQuizAttempt(QuizQuestionReqDTO questionDto, 
                                        QuizResult quizResult, 
                                        Question originalQuestion) {
        QuizAttempt attempt = new QuizAttempt();
        
        // Snapshot the question if it exists
        if (originalQuestion != null) {
            attempt.snapshotQuestion(originalQuestion);
            attempt.setOriginalQuestion(originalQuestion);
        }
        
        // Snapshot the answers if they exist
        if (originalQuestion != null && originalQuestion.getAnswers() != null) {
            attempt.snapshotAnswers(originalQuestion.getAnswers());
        }
        
        // Initialize attempt without user response
        attempt.setSelectedAnswer(null);
        attempt.setCorrect(false);
        attempt.setSkipped(false);
        
        // Set relationships
        attempt.setQuizResult(quizResult);
        
        return attempt;
    }

    /**
     * Process quiz answers, handling existing vs custom content
     */
    private Set<QuizAnswer> processQuizAnswers(Set<QuizAnswerReqDTO> answerDtos) {
        return answerDtos.stream()
                .map(this::processQuizAnswer)
                .collect(Collectors.toSet());
    }

    /**
     * Process a single quiz answer, creating QuizAnswer with proper relationships
     */
    private QuizAnswer processQuizAnswer(QuizAnswerReqDTO answerDto) {
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setCustom(answerDto.isCustom());
        quizAnswer.setCorrect(answerDto.isCorrect());

        if (answerDto.isCustom()) {
            // Custom answer - set custom answer text
            quizAnswer.setCustomAnswer(answerDto.getCustomAnswer());
            quizAnswer.setAnswer(null); // No existing answer reference
        } else {
            // Existing answer - validate and set reference
            if (answerDto.getAnswerId() == null) {
                throw new RuntimeException("Answer ID is required when isCustom is false");
            }
            
            Answer existingAnswer = answerRepo.findById(answerDto.getAnswerId())
                    .orElseThrow(() -> new NotFoundException("Answer not found with id: " + answerDto.getAnswerId()));
            
            quizAnswer.setAnswer(existingAnswer);
            quizAnswer.setCustomAnswer(null);
        }

        return quizAnswer;
    }

    /**
     * Map quiz to DTO with all related data
     */
    private QuizResDTO mapToDtoWithRelations(Quiz quiz) {
        QuizResDTO dto = quizMapper.toDto(quiz);
        // Additional mapping logic can be added here if needed
        return dto;
    }
} 