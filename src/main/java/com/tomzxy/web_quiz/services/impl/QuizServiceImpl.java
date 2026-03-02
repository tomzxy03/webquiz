package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.QuizMapper;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionId;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Subject;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.repositories.LobbyRepo;
import com.tomzxy.web_quiz.repositories.QuestionRepo;
import com.tomzxy.web_quiz.repositories.SubjectRepo;
import com.tomzxy.web_quiz.repositories.QuizQuestionLinkRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepo quizRepo;
    private final UserRepo userRepo;
    private final LobbyRepo lobbyRepo;
    private final QuestionRepo questionRepo;
    private final SubjectRepo subjectRepo;
    private final QuizQuestionLinkRepo quizQuestionLinkRepo;
    private final QuizMapper quizMapper;
    private final ConvertToPageResDTO convertToPageResDTO;

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> quizzes = quizRepo.findAll(pageable);
        return convertToPageResDTO.convertPageResponse(quizzes, pageable, quizMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResDTO getById(Long id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));
        return quizMapper.toDto(quiz);
    }

    @Override
    @Transactional
    public QuizResDTO create(QuizReqDTO dto) {
        // Validate host exists
        User host = userRepo.findById(dto.getHostId())
                .orElseThrow(() -> new NotFoundException("Host user not found with id: " + dto.getHostId()));

        // Validate subject exists
        Subject subject = subjectRepo.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found with id: " + dto.getSubjectId()));

        // Validate group if provided
        Lobby group = null;
        if (dto.getLobbyId() != null) {
            group = lobbyRepo.findById(dto.getLobbyId())
                    .orElseThrow(() -> new NotFoundException("Group not found with id: " + dto.getLobbyId()));
        }

        // Create quiz entity
        Quiz quiz = quizMapper.toEntity(dto);
        quiz.setHost(host);
        quiz.setSubject(subject);
        quiz.setLobby(group);

        quiz = quizRepo.save(quiz);

        // Process question links
        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            createQuestionLinks(quiz, dto.getQuestionIds());
        }

        return quizMapper.toDto(quiz);
    }

    @Override
    @Transactional
    public QuizResDTO update(Long id, QuizReqDTO dto) {
        Quiz existingQuiz = quizRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));

        // Validate host exists
        User host = userRepo.findById(dto.getHostId())
                .orElseThrow(() -> new NotFoundException("Host user not found with id: " + dto.getHostId()));

        // Validate subject exists
        Subject subject = subjectRepo.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found with id: " + dto.getSubjectId()));

        // Validate group if provided
        Lobby group = null;
        if (dto.getLobbyId() != null) {
            group = lobbyRepo.findById(dto.getLobbyId())
                    .orElseThrow(() -> new NotFoundException("Group not found with id: " + dto.getLobbyId()));
        }

        // Update basic fields
        existingQuiz.setTitle(dto.getTitle());
        existingQuiz.setDescription(dto.getDescription());
        existingQuiz.setTimeLimitMinutes(dto.getTimeLimitMinutes());
        existingQuiz.setVisibility(dto.getVisibility());
        existingQuiz.setMaxAttempt(dto.getMaxAttempts() != null ? dto.getMaxAttempts() : 1);
        existingQuiz.setHost(host);
        existingQuiz.setSubject(subject);
        existingQuiz.setLobby(group);

        if (dto.getStartDate() != null) {
            existingQuiz.setStartDate(dto.getStartDate());
        }

        existingQuiz = quizRepo.save(existingQuiz);

        // Re-create question links if provided
        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            // Remove old links
            existingQuiz.getQuizQuestionLinks().clear();
            quizRepo.save(existingQuiz);
            // Create new links
            createQuestionLinks(existingQuiz, dto.getQuestionIds());
        }

        return quizMapper.toDto(existingQuiz);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));
        quiz.deactivate();
        quizRepo.save(quiz);
    }

    /**
     * Create QuizQuestionLink entries for each question ID in the set.
     */
    private void createQuestionLinks(Quiz quiz, Set<Long> questionIds) {
        for (Long questionId : questionIds) {
            Question question = questionRepo.findById(questionId)
                    .orElseThrow(() -> new NotFoundException("Question not found with id: " + questionId));

            QuizQuestionId linkId = new QuizQuestionId(quiz.getId(), question.getId());
            QuizQuestionLink link = QuizQuestionLink.builder()
                    .id(linkId)
                    .quiz(quiz)
                    .question(question)
                    .points(question.getPoints() != null ? question.getPoints() : 1)
                    .build();

            quizQuestionLinkRepo.save(link);
        }
    }
}