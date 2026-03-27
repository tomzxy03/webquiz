package com.tomzxy.web_quiz.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tomzxy.web_quiz.dto.requests.filter.QuizFilterReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizQuestionReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.IdentityType;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.enums.QuizStatus;
import com.tomzxy.web_quiz.enums.QuizVisibility;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.ExistedException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.AnswerMapper;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
import com.tomzxy.web_quiz.mapstructs.QuizMapper;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionId;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.Quiz.QuizSpecification;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.IdentityContext;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Subject;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.QuestionService;
import com.tomzxy.web_quiz.services.QuizService;
import com.tomzxy.web_quiz.services.common.ConvertToPageResDTO;
import com.tomzxy.web_quiz.mapstructs.QuizInstanceMapper;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.utils.SecurityUtils;
import com.tomzxy.web_quiz.utils.SpecificationUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final QuizInstanceRepo quizInstanceRepo;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final QuizInstanceMapper quizInstanceMapper;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final QuestionService questionService;

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> quizzes = quizRepo.findAllActive(pageable);
        return convertToPageResDTO.convertPageResponse(quizzes, pageable, quizMapper::toDto);
    }

    @Override
    @Transactional
    public PageResDTO<?> getAllWithFilter(QuizFilterReqDTO filter, int page, int size) {
        log.info("Get all quizzes with filter");
        if (filter.getMinQuestions() != null &&
                filter.getMaxQuestions() != null &&
                filter.getMinQuestions() > filter.getMaxQuestions()) {

            throw new ApiException(AppCode.BAD_REQUEST, "minQuestions cannot be greater than maxQuestions");
        }
        if (filter.getSubjectId() != null &&
                !subjectRepo.existsById(filter.getSubjectId())) {

            throw new NotFoundException("Subject not found");
        }
        Pageable pageable = PageRequest.of(page, size);
        Specification<Quiz> spec = QuizSpecification.filter(filter);
        spec = SpecificationUtils.and(spec,
                SpecificationUtils.equal("status", QuizStatus.OPENED));
        spec = SpecificationUtils.and(spec,
                SpecificationUtils.equal("visibility", QuizVisibility.PUBLIC));
        Page<Quiz> quizzes = quizRepo.findAll(spec, pageable);
        return convertToPageResDTO.convertPageResponse(quizzes, pageable, quizMapper::toDto);

    }

    @Override
    public PageResDTO<?> getLatestQuizzes(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Quiz> spec = null;
        spec = SpecificationUtils.and(spec, SpecificationUtils.isTrue("isActive"));
        spec = SpecificationUtils.and(spec, SpecificationUtils.equal("status", QuizStatus.OPENED));
        spec = SpecificationUtils.and(spec, SpecificationUtils.equal("visibility", QuizVisibility.PUBLIC));
        Page<Quiz> quizzes = quizRepo.findAll(
                spec,
                pageable);
        return convertToPageResDTO.convertPageResponse(quizzes, pageable, quizMapper::toDto);
    }

    @Override
    public PageResDTO<?> getPopularQuizzes(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size);
        LocalDateTime since = LocalDateTime.now().minusDays(30);

        Page<Quiz> quizzes = quizRepo.findPopularSince(since, pageable);
        return convertToPageResDTO.convertPageResponse(quizzes, pageable, quizMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDetailResDTO getQuizDetail(Long id, IdentityContext identity) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + id));

        QuizResDTO quizDto = quizMapper.toDto(quiz);
        Long userId = null;
        String guestId = null;
        long attemptCount = 0L;
        if (identity.getType() == IdentityType.USER && identity.getUserId() != null) {
            userId = identity.getUserId();
            attemptCount = getAttemptCount(quiz.getId(), userId);
        } else if (identity.getType() == IdentityType.GUEST && identity.getGuestId() != null) {
            guestId = identity.getGuestId();
            attemptCount = getAttemptCount(quiz.getId(), guestId);
        }
        String attemptState = "NONE";
        Long instanceId = null;

        if (userId != null) {
            Optional<QuizInstance> activeInstance = quizInstanceRepo.findByQuizIdAndUserIdAndStatus(id, userId,
                    QuizInstanceStatus.IN_PROGRESS);
            if (activeInstance.isPresent()) {
                attemptState = "IN_PROGRESS";
                instanceId = activeInstance.get().getId();
            }
        } else if (guestId != null) {
            Optional<QuizInstance> activeInstance = quizInstanceRepo.findByQuizIdAndGuestIdAndStatus(id, guestId,
                    QuizInstanceStatus.IN_PROGRESS);
            if (activeInstance.isPresent()) {
                attemptState = "IN_PROGRESS";
                instanceId = activeInstance.get().getId();
            }
        }

        return QuizDetailResDTO.builder()
                .quiz(quizDto)
                .quizConfig(quiz.getConfig())
                .attemptState(attemptState)
                .totalAttempt(Math.toIntExact(attemptCount))
                .instanceId(instanceId)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizInstanceResDTO getActiveInstance(Long quizId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new ApiException(AppCode.UNAUTHORIZED, "User must be logged in to resume a quiz");
        }

        QuizInstance instance = quizInstanceRepo
                .findByQuizIdAndUserIdAndStatus(quizId, userId, QuizInstanceStatus.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("No active instance found for this quiz"));

        return quizInstanceMapper.toQuizInstanceResDTO(instance);
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

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new ApiException(AppCode.UNAUTHORIZED, "User not authenticated");
        }

        User host = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Host user not found: " + userId));

        Subject subject = subjectRepo.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found: " + dto.getSubjectId()));

        Quiz quiz = quizMapper.toEntity(dto);

        quiz.setHost(host);
        quiz.setSubject(subject);

        quiz = quizRepo.save(quiz);

        return quizMapper.toDto(quiz);
    }

    @Override
    @Transactional
    public void create_Questions(Long quizId, List<QuizQuestionReqDTO> dtos) {

        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        List<Question> newQuestions = new ArrayList<>();
        List<Question> resolvedQuestions = new ArrayList<>();
        List<Long> resolvedPoints = new ArrayList<>();

        // preload existing question
        List<Long> questionIds = dtos.stream()
                .map(QuizQuestionReqDTO::getQuestionId)
                .filter(Objects::nonNull)
                .toList();

        if (new HashSet<>(questionIds).size() != questionIds.size()) {
            throw new ExistedException(
                    AppCode.DATA_EXISTED,
                    "Duplicate question in request");
        }

        Map<Long, Question> questionMap = questionRepo.findAllById(questionIds)
                .stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        for (QuizQuestionReqDTO dto : dtos) {

            Question question;

            // CASE 1: dùng question có sẵn
            if (dto.getQuestionId() != null) {

                question = questionMap.get(dto.getQuestionId());

                if (question == null) {
                    throw new NotFoundException("Question not found: " + dto.getQuestionId());
                }

            }
            // CASE 2: tạo question mới
            else if (dto.getQuestionReqDTO() != null) {

                question = questionMapper.toQuestion(dto.getQuestionReqDTO());

                if (dto.getQuestionReqDTO().getAnswers() != null) {
                    Set<Answer> answers = dto.getQuestionReqDTO().getAnswers().stream()
                            .map(answerMapper::toAnswer)
                            .collect(Collectors.toSet());

                    question.addAnswers(answers);
                }

                String hash = questionService.generateContentHash(question);
                question.setContentHash(hash);
                newQuestions.add(question);

            } else {
                throw new ApiException(AppCode.INTERNAL_ERROR, "Invalid question input");
            }

            resolvedQuestions.add(question);
            resolvedPoints.add(dto.getPoints());
        }

        // Check duplicate contentHash trong DB
        if (!newQuestions.isEmpty()) {

            List<String> hashes = newQuestions.stream()
                    .map(Question::getContentHash)
                    .toList();

            // check duplicate trong request
            Set<String> uniqueHashes = new HashSet<>(hashes);
            if (uniqueHashes.size() != hashes.size()) {
                throw new ExistedException(
                        AppCode.DATA_EXISTED,
                        "Duplicate question in request");
            }

            List<String> existedHashes = questionRepo.findAllByContentHashIn(hashes);

            if (!existedHashes.isEmpty()) {
                throw new ExistedException(
                        AppCode.DATA_EXISTED,
                        "Question content duplicated");
            }

            questionRepo.saveAll(newQuestions);
            quiz.setStatus(QuizStatus.OPENED);
        }

        List<QuizQuestionLink> links = new ArrayList<>();

        for (int i = 0; i < resolvedQuestions.size(); i++) {

            Question question = resolvedQuestions.get(i);
            Long points = resolvedPoints.get(i);
            QuizQuestionId linkId = new QuizQuestionId(
                    quiz.getId(),
                    question.getId());
            QuizQuestionLink link = new QuizQuestionLink();
            link.setId(linkId);
            link.setQuiz(quiz);
            link.setQuestion(question);
            link.setPoints(points != null ? points : 1L);

            links.add(link);
        }
        quiz.setTotalQuestion(links.size());
        quizRepo.save(quiz);
        quizQuestionLinkRepo.saveAll(links);
    }

    @Override
    @Transactional
    public QuizResDTO update(Long id, QuizReqDTO dto) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + id));

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new ApiException(AppCode.UNAUTHORIZED, "User not authenticated");
        }

        if (!quiz.getHost().getId().equals(userId)) {
            throw new ApiException(AppCode.FORBIDDEN, "User is not authorized to update this quiz");
        }

        Subject subject = subjectRepo.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found: " + dto.getSubjectId()));

        // update basic info
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTimeLimitMinutes(dto.getTimeLimitMinutes());
        quiz.setVisibility(dto.getVisibility());
        quiz.setMaxAttempt(dto.getMaxAttempt() != null ? dto.getMaxAttempt() : 1);
        quiz.setSubject(subject);

        if (dto.getStartAt() != null) {
            quiz.setStartDate(dto.getStartAt());
        }

        if (dto.getEndAt() != null) {
            quiz.setEndDate(dto.getEndAt());
        }

        quizRepo.save(quiz);

        return quizMapper.toDto(quiz);
    }

    @Override
    @Transactional
    public void update_Questions(Long quizId, List<QuizQuestionReqDTO> questionReqDTO) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));
        if (questionReqDTO == null || questionReqDTO.isEmpty()) {
            throw new ApiException(AppCode.BAD_REQUEST, "Question list cannot be empty");
        }

        // Clear existing questions
        quizQuestionLinkRepo.deleteAllByQuizId(quizId);

        // Create new question links
        create_Questions(quizId, questionReqDTO);
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

    public long getAttemptCount(Long quizId, Long userId) {
        return quizInstanceRepo.countByQuizIdAndUserIdAndStatusIn(
                quizId, userId,
                List.of(QuizInstanceStatus.TIMED_OUT, QuizInstanceStatus.SUBMITTED));
    }

    private long getAttemptCount(Long quizId, String guestId) {
        return quizInstanceRepo.countByQuizIdAndGuestIdAndStatusIn(
                quizId, guestId,
                List.of(QuizInstanceStatus.TIMED_OUT, QuizInstanceStatus.SUBMITTED));
    }

}
