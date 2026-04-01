package com.tomzxy.web_quiz.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizAnswerReqDTO;
import com.tomzxy.web_quiz.dto.responses.*;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizStateResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionResultResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.IdentityType;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.enums.QuizStatus;
import com.tomzxy.web_quiz.enums.ResponseStatus;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;
import com.tomzxy.web_quiz.models.snapshot.AnswerRecord;
import com.tomzxy.web_quiz.models.snapshot.AnswerSnapshot;
import com.tomzxy.web_quiz.models.snapshot.QuestionSnapshot;
import com.tomzxy.web_quiz.models.snapshot.QuizQuestionSnapshot;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import com.tomzxy.web_quiz.services.common.ConvertToPageResDTO;
import com.tomzxy.web_quiz.mapstructs.QuizInstanceMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizInstanceServiceImpl implements QuizInstanceService {

    private final QuizInstanceRepo quizInstanceRepo;
    private final QuizUserResponseRepo quizUserResponseRepo;
    private final QuizRepo quizRepo;
    private final UserRepo userRepo;
    private final QuizInstanceMapper quizInstanceMapper;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEvent;
    @PersistenceContext
    private EntityManager entityManager;

    private static final String ANSWERS_KEY_PREFIX = "quiz_instance:";
    private static final String ANSWERS_KEY_SUFFIX = ":answers";
    private static final String SNAPSHOT_KEY_SUFFIX = ":snapshot";
    private static final String ACTIVE_INSTANCES_KEY = "quiz:active_instances";
    private static final String LOCK_KEY_PREFIX = "quiz_instance:lock:";

    // ============ Lua script for atomic submit ============
    private static final String GET_AND_DELETE_LUA = "local data = redis.call('HGETALL', KEYS[1]); " +
            "redis.call('DEL', KEYS[1]); " +
            "return data;";

    // ============ Helper: Redis key builders ============
    private String answersKey(Long instanceId) {
        return ANSWERS_KEY_PREFIX + instanceId + ANSWERS_KEY_SUFFIX;
    }

    private String snapshotKey(Long instanceId) {
        return ANSWERS_KEY_PREFIX + instanceId + SNAPSHOT_KEY_SUFFIX;
    }

    private String lockKey(Long instanceId) {
        return LOCK_KEY_PREFIX + instanceId;
    }

    private IdentityContext requireIdentity(IdentityContext identity) {
        if (identity == null || identity.getType() == null) {
            throw new ApiException(AppCode.UNAUTHORIZED, "Identity is required");
        }
        if (identity.getType() == IdentityType.USER) {
            if (identity.getUserId() == null) {
                throw new ApiException(AppCode.UNAUTHORIZED, "User identity is required");
            }
        } else if (identity.getType() == IdentityType.GUEST) {
            if (identity.getGuestId() == null || identity.getGuestId().isBlank()) {
                throw new ApiException(AppCode.UNAUTHORIZED, "Guest identity is required");
            }
        }
        return identity;
    }

    private Optional<QuizInstance> findActiveInstance(Long quizId, IdentityContext identity) {
        if (identity.getType() == IdentityType.USER) {
            return quizInstanceRepo.findTopByQuizIdAndUserIdAndStatusOrderByStartedAtDesc(
                    quizId, identity.getUserId(), QuizInstanceStatus.IN_PROGRESS);
        }
        if (identity.getType() == IdentityType.GUEST) {
            return quizInstanceRepo.findTopByQuizIdAndGuestIdAndStatusOrderByStartedAtDesc(
                    quizId, identity.getGuestId(), QuizInstanceStatus.IN_PROGRESS);
        }
        return Optional.empty();
    }

    private long countAttempts(Long quizId, IdentityContext identity) {
        if (identity.getType() == IdentityType.USER) {
            return quizInstanceRepo.countByQuizIdAndUserIdAndStatusIn(
                    quizId, identity.getUserId(),
                    List.of(QuizInstanceStatus.SUBMITTED, QuizInstanceStatus.TIMED_OUT));
        }
        if (identity.getType() == IdentityType.GUEST) {
            return quizInstanceRepo.countByQuizIdAndGuestIdAndStatusIn(
                    quizId, identity.getGuestId(),
                    List.of(QuizInstanceStatus.SUBMITTED, QuizInstanceStatus.TIMED_OUT));
        }
        return 0L;
    }

    private void assertOwnership(QuizInstance instance, IdentityContext identity, String action) {
        if (identity.getType() == IdentityType.USER) {
            if (instance.getUser() == null || !instance.getUser().getId().equals(identity.getUserId())) {
                throw new ApiException(AppCode.FORBIDDEN, "No permission to " + action);
            }
            return;
        }
        if (identity.getType() == IdentityType.GUEST) {
            if (instance.getGuestId() == null || !instance.getGuestId().equals(identity.getGuestId())) {
                throw new ApiException(AppCode.FORBIDDEN, "No permission to " + action);
            }
            return;
        }
        throw new ApiException(AppCode.FORBIDDEN, "No permission to " + action);
    }

    // ============ 3.1 Start Quiz ============
    @Override
    @Transactional
    public QuizInstanceResDTO createQuizInstance(Long quizId, IdentityContext identity) {
        log.info("Starting quiz instance for quiz: {}", quizId);
        IdentityContext ctx = requireIdentity(identity);
        Long userId = ctx.getType() == IdentityType.USER ? ctx.getUserId() : null;
        String guestId = ctx.getType() == IdentityType.GUEST ? ctx.getGuestId() : null;

        // 1. Check existing IN_PROGRESS attempt
        Optional<QuizInstance> existing = findActiveInstance(quizId, ctx);
        if (existing.isPresent()) {
            QuizInstance instance = existing.get();
            QuizInstanceResDTO res = quizInstanceMapper.toQuizInstanceResDTO(instance);
            res.setQuestions(mapSnapshotToQuestionDTOs(instance.getSnapshot()));
            res.setRemainingTimeSeconds(calculateRemainingTime(instance));
            return res;
        }

        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        // 1b. Check quiz status = OPENED (#5)
        if (quiz.getStatus() != QuizStatus.OPENED) {
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz is not open for attempts");
        }

        // 2. Check attempt limit (#4: null guard for maxAttempt)
        if (quiz.getMaxAttempt() != null && quiz.getMaxAttempt() > 0) {
            long count = countAttempts(quizId, ctx);
            if (count >= quiz.getMaxAttempt()) {
                throw new ApiException(AppCode.BAD_REQUEST, "Maximum attempt limit reached");
            }
        }

        // 3. Generate Snapshot
        QuizQuestionSnapshot snapshot = generateSnapshot(quiz);

        // 4. Create QuizInstance in DB
        QuizInstance instance = QuizInstance.builder()
                .quiz(quiz)
                .user(userId != null ? userRepo.findById(userId).orElse(null) : null)
                .guestId(userId == null ? guestId : null)
                .startedAt(LocalDateTime.now())
                .status(QuizInstanceStatus.IN_PROGRESS)
                .totalPoints(snapshot.getTotalPoints())
                .totalQuestions(snapshot.getQuestions() != null ? (long) snapshot.getQuestions().size() : 0L)
                .correctAnswers(0L)
                .earnedPoints(0L)
                .snapshot(snapshot)
                .build();

        try {
            instance = quizInstanceRepo.save(instance);
        } catch (DataIntegrityViolationException e) {
            // Race condition: instance đã được tạo bởi request khác
            instance = findActiveInstance(quizId, ctx)
                    .orElseThrow(() -> new ApiException(AppCode.BAD_REQUEST, "Failed to create quiz instance"));
        }

        // 5. Cache snapshot in Redis
        int durationMinutes = snapshot.getDuration() != null ? snapshot.getDuration() : 60;
        long ttlSeconds = (long) durationMinutes * 60 + 3600; // duration + 1 hour buffer

        try {
            String snapshotJson = objectMapper.writeValueAsString(snapshot);
            stringRedisTemplate.opsForValue().set(
                    snapshotKey(instance.getId()),
                    snapshotJson,
                    Duration.ofSeconds(ttlSeconds));
        } catch (JsonProcessingException e) {
            log.warn("Failed to cache snapshot in Redis for instance {}", instance.getId(), e);
        }

        // 6. Set TTL for answers key (will be created on first answer)
        // TTL will be set when first answer is saved

        // 7. Add to active instances Sorted Set
        long endTimeEpoch = Instant.now().getEpochSecond() + (long) durationMinutes * 60;
        stringRedisTemplate.opsForZSet().add(
                ACTIVE_INSTANCES_KEY,
                instance.getId().toString(),
                endTimeEpoch);
        QuizInstanceResDTO res = quizInstanceMapper.toQuizInstanceResDTO(instance);
        res.setQuestions(mapSnapshotToQuestionDTOs(snapshot));
        res.setQuestionLayout(quiz.getQuestionLayout() != null ? quiz.getQuestionLayout() : null);
        res.setTimeLimitMinutes(quiz.getTimeLimitMinutes());
        if (quiz.getTimeLimitMinutes() != null) {
            res.setRemainingTimeSeconds(quiz.getTimeLimitMinutes() * 60L);
        }
        return res;
    }

    private QuizQuestionSnapshot generateSnapshot(Quiz quiz) {
        QuizQuestionSnapshot snapshot = new QuizQuestionSnapshot();
        snapshot.setQuizId(quiz.getId());
        snapshot.setGeneratedAt(LocalDateTime.now());
        snapshot.setVersion(1);
        snapshot.setDuration(quiz.getTimeLimitMinutes());

        boolean shuffleQuestions = quiz.getConfig() != null
                && Boolean.TRUE.equals(quiz.getConfig().getShuffleQuestions());
        boolean shuffleAnswers = quiz.getConfig() != null && Boolean.TRUE.equals(quiz.getConfig().getShuffleAnswers());
        snapshot.setShuffledQuestions(shuffleQuestions);
        snapshot.setShuffledAnswers(shuffleAnswers);

        List<QuizQuestionLink> links = new ArrayList<>(quiz.getQuizQuestionLinks());
        if (shuffleQuestions) {
            Collections.shuffle(links);
        } else {
            // Ensure deterministic order if not shuffled
            links.sort(Comparator.comparing(link -> link.getQuestion().getId()));
        }

        List<QuestionSnapshot> questionSnapshots = new ArrayList<>();
        Long totalPoints = 0L;

        for (int i = 0; i < links.size(); i++) {
            QuizQuestionLink link = links.get(i);
            Question question = link.getQuestion();

            QuestionSnapshot qs = new QuestionSnapshot();
            if (question.getContentHash() == null || question.getContentHash().isBlank()) {
                throw new ApiException(AppCode.INTERNAL_ERROR, "Question content hash missing for snapshot");
            }
            qs.setKey(UUID.randomUUID().toString()); // or use question.getContentHash()
            qs.setContent(question.getQuestionName());
            qs.setType(question.getType());
            qs.setAnswerType(question.getAnswerType());
            qs.setOrderIndex(i + 1);
            qs.setPoints(link.getPoints());
            totalPoints += link.getPoints();

            List<Answer> answers = new ArrayList<>(question.getAnswers());
            if (shuffleAnswers) {
                Collections.shuffle(answers);
            } else {
                // Ensure deterministic order if not shuffled
                answers.sort(Comparator.comparing(Answer::getId));
            }

            List<AnswerSnapshot> answerSnapshots = new ArrayList<>();
            List<String> correctAnsIds = new ArrayList<>();

            for (int j = 0; j < answers.size(); j++) {
                Answer a = answers.get(j);
                AnswerSnapshot as = new AnswerSnapshot();
                as.setSnapshotId(UUID.randomUUID().toString());
                as.setOriginalAnswerId(a.getId());
                as.setContent(a.getAnswerName());
                as.setOrderIndex(j + 1);
                as.setType(a.getType());
                answerSnapshots.add(as);

                if (a.isAnswerCorrect()) {
                    correctAnsIds.add(String.valueOf(j)); // 0-based index as correct answer
                }
            }
            qs.setAnswers(answerSnapshots);
            qs.setCorrectAnswerIds(correctAnsIds);
            questionSnapshots.add(qs);
        }

        snapshot.setQuestions(questionSnapshots);
        snapshot.setTotalPoints(totalPoints);
        return snapshot;
    }

    /**
     * Chuyển đổi từ Snapshot (Dữ liệu gốc trong DB) sang Question DTOs (Dữ liệu trả
     * về Client)
     */
    private List<QuizInstanceQuestionResDTO> mapSnapshotToQuestionDTOs(QuizQuestionSnapshot snapshot) {
        if (snapshot == null || snapshot.getQuestions() == null)
            return new ArrayList<>();

        List<QuizInstanceQuestionResDTO> questionDTOs = new ArrayList<>();
        List<QuestionSnapshot> questions = snapshot.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            QuestionSnapshot qs = questions.get(i);

            QuizInstanceQuestionResDTO qDTO = new QuizInstanceQuestionResDTO();
            qDTO.setId(null);
            qDTO.setSnapshotKey(qs.getKey());
            qDTO.setDisplayOrder(i + 1);
            qDTO.setPoints(qs.getPoints());
            qDTO.setQuestionText(qs.getContent());
            qDTO.setType(qs.getType().name());
            qDTO.setAnswerType(qs.getAnswerType() != null ? qs.getAnswerType().name() : null);

            List<QuizInstanceAnswerResDTO> answerDTOs = new ArrayList<>();
            for (int j = 0; j < qs.getAnswers().size(); j++) {
                AnswerSnapshot as = qs.getAnswers().get(j);
                QuizInstanceAnswerResDTO aDTO = new QuizInstanceAnswerResDTO();

                // QUAN TRỌNG: ID của câu trả lời ở đây phải là INDEX (j)
                // vì logic tính điểm của bạn đang lưu correct_answer là 0-based index
                aDTO.setId((long) j);
                aDTO.setDisplayOrder(j + 1);
                aDTO.setAnswerText(as.getContent());
                aDTO.setOptionLabel(String.valueOf((char) ('A' + j))); // A, B, C, D...

                answerDTOs.add(aDTO);
            }
            qDTO.setAnswers(answerDTOs);
            questionDTOs.add(qDTO);
        }
        return questionDTOs;
    }

    /**
     * Tính toán số giây còn lại dựa trên thời điểm bắt đầu
     */
    private Long calculateRemainingTime(QuizInstance instance) {
        if (instance.getQuiz().getTimeLimitMinutes() == null)
            return null;

        long limitSeconds = instance.getQuiz().getTimeLimitMinutes() * 60L;
        long elapsedSeconds = java.time.Duration.between(instance.getStartedAt(), LocalDateTime.now()).getSeconds();
        long remaining = limitSeconds - elapsedSeconds;

        return Math.max(0, remaining);
    }

    @Override
    public QuizInstanceResDTO createQuizInstance(QuizInstanceReqDTO request, IdentityContext identity) {
        return createQuizInstance(request.getQuizId(), identity);
    }

    // ============ 3.2 Answer Question ============
    @Override
    @Transactional
    public void saveAnswer(Long instanceId, IdentityContext identity, QuizAnswerReqDTO request) {
        log.info("Saving answer for instance: {}, snapshotKey: {}", instanceId, request.getQuestionSnapshotKey());
        IdentityContext ctx = requireIdentity(identity);

        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        // 1. Check status
        if (!instance.isInProgress()) {
            log.warn("Attempt to save answer for non-in-progress instance: {}", instanceId);
            return; // Silently ignore saves for non-in-progress instances
        }

        // 2. Check ownership
        assertOwnership(instance, ctx, "answer this quiz");

        // 3. Check time: current_time <= start_time + duration*60
        QuizQuestionSnapshot snapshot = instance.getSnapshot();
        if (snapshot == null) {
            throw new ApiException(AppCode.BAD_REQUEST, "Snapshot not found for this instance");
        }

        if (request.getQuestionSnapshotKey() == null || request.getQuestionSnapshotKey().isBlank()) {
            throw new ApiException(AppCode.BAD_REQUEST, "Question snapshot key is required");
        }

        if (snapshot.getDuration() != null) {
            long startEpoch = java.sql.Timestamp.valueOf(instance.getStartedAt()).toInstant().getEpochSecond();
            long endEpoch = startEpoch + (long) snapshot.getDuration() * 60;
            if (Instant.now().getEpochSecond() > endEpoch) {
                throw new ApiException(AppCode.BAD_REQUEST, "Time has expired. Please submit your quiz.");
            }
        }

        // 4. Resolve question in snapshot
        QuestionSnapshot questionSnapshot = resolveQuestionSnapshot(snapshot, request);
        String questionKey = questionSnapshot.getKey();

        // 5. Validate answer indices
        List<Integer> answerIndices = request.getAnswer();
        int optionsCount = questionSnapshot.getAnswers().size();
        for (Integer idx : answerIndices) {
            if (idx < 0 || idx >= optionsCount) {
                throw new ApiException(AppCode.BAD_REQUEST,
                        "Invalid answer index: " + idx + ". Must be in range [0, " + (optionsCount - 1) + "]");
            }
        }

        // 6. For SINGLE_CHOICE, only 1 answer allowed
        if (questionSnapshot.getAnswerType() != null
                && "SINGLE_CHOICE".equals(questionSnapshot.getAnswerType().name())
                && answerIndices.size() > 1) {
            throw new ApiException(AppCode.BAD_REQUEST, "Single choice question allows only 1 answer");
        }

        // 7. Create AnswerRecord and store in Redis Hash
        AnswerRecord record = AnswerRecord.builder()
                .answer(answerIndices)
                .answeredAt(Instant.now())
                .build();

        try {
            String recordJson = objectMapper.writeValueAsString(record);
            String redisKey = answersKey(instanceId);
            stringRedisTemplate.opsForHash().put(redisKey, questionKey, recordJson);

            // Set TTL only if not already set (first answer)
            Long ttl = stringRedisTemplate.getExpire(redisKey);
            if (ttl == null || ttl <= 0) {
                int durationMinutes = snapshot.getDuration() != null ? snapshot.getDuration() : 60;
                stringRedisTemplate.expire(redisKey, Duration.ofSeconds((long) durationMinutes * 60 + 3600));
            }
        } catch (JsonProcessingException e) {
            throw new ApiException(AppCode.INTERNAL_ERROR, "Failed to serialize answer");
        }
    }

    private QuestionSnapshot resolveQuestionSnapshot(QuizQuestionSnapshot snapshot, QuizAnswerReqDTO request) {
        return snapshot.getQuestions().stream()
                .filter(q -> request.getQuestionSnapshotKey().equals(q.getKey()))
                .findFirst()
                .orElseThrow(() -> new ApiException(AppCode.BAD_REQUEST, "Question not found in snapshot"));
    }

    // ============ 3.3 Resume / Get State ============
    @Override
    @Transactional(readOnly = true)
    public QuizStateResDTO getQuizState(Long instanceId, IdentityContext identity) {
        IdentityContext ctx = requireIdentity(identity);
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        // 1. Check ownership and status
        assertOwnership(instance, ctx, "access this quiz instance");
        if (!instance.isInProgress()) {
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz instance is no longer in progress");
        }

        // 2. Get snapshot (try Redis cache first, fallback to DB)
        QuizQuestionSnapshot snapshot = getSnapshotFromCacheOrDb(instance);

        // 3. Calculate remaining time
        long remainingSeconds = 0;
        if (snapshot.getDuration() != null) {
            long startEpoch = instance.getStartedAt()
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();

            long endEpoch = startEpoch + snapshot.getDuration() * 60L;

            remainingSeconds = Math.max(0, endEpoch - Instant.now().getEpochSecond());
        }

        // 4. Get saved answers from Redis
        String redisKey = answersKey(instanceId);
        Map<Object, Object> redisAnswers = stringRedisTemplate.opsForHash().entries(redisKey);
        Map<String, AnswerRecord> answerMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : redisAnswers.entrySet()) {
            try {
                AnswerRecord record = objectMapper.readValue((String) entry.getValue(), AnswerRecord.class);
                answerMap.put((String) entry.getKey(), record);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse answer record for question {}", entry.getKey());
            }
        }

        // 5. Build response (no correct answers exposed)
        List<QuizStateResDTO.QuizStateQuestionDTO> questions = snapshot.getQuestions().stream()
                .map(qs -> {
                    List<QuizStateResDTO.QuizStateAnswerDTO> answerDtos = new ArrayList<>();
                    for (int i = 0; i < qs.getAnswers().size(); i++) {
                        AnswerSnapshot as = qs.getAnswers().get(i);
                        answerDtos.add(QuizStateResDTO.QuizStateAnswerDTO.builder()
                                .index(i)
                                .content(as.getContent())
                                .type(as.getType() != null ? as.getType().name() : null)
                                .build());
                    }

                    AnswerRecord userRecord = answerMap.get(qs.getKey());
                    return QuizStateResDTO.QuizStateQuestionDTO.builder()
                            .questionId(qs.getKey())
                            .content(qs.getContent())
                            .type(qs.getType() != null ? qs.getType().name() : null)
                            .answerType(qs.getAnswerType() != null ? qs.getAnswerType().name() : null)
                            .orderIndex(qs.getOrderIndex())
                            .points(qs.getPoints())
                            .answers(answerDtos)
                            .userAnswer(userRecord != null ? userRecord.getAnswer() : null)
                            .build();
                })
                .collect(Collectors.toList());

        return QuizStateResDTO.builder()
                .instanceId(instanceId)
                .status(instance.getStatus().name())
                .remainingSeconds(remainingSeconds)
                .questions(questions)
                .build();
    }

    // ============ 3.4 Submit Quiz ============
    @Override
    @Transactional
    public QuizResultDetailResDTO submitQuiz(Long instanceId, IdentityContext identity) {
        IdentityContext ctx = requireIdentity(identity);
        String submitLockKey = "quiz:instance:" + instanceId + ":submit-lock";

        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        // Verify ownership early
        assertOwnership(instance, ctx, "submit this quiz");

        // Idempotent submit: if already completed, return existing result
        if (instance.isSubmitted() || instance.isTimedOut()) {
            log.info("Submit idempotent return for instance {} (status={})", instanceId, instance.getStatus());
            return mapResultBasedOnConfig(instance, instance.getStatus());
        }

        if (!instance.isInProgress()) {
            log.warn("Submit rejected: instance {} not in progress (status={})", instanceId, instance.getStatus());
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz instance is no longer in progress");
        }

        // Acquire submit lock (prevent double submit)
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(submitLockKey, "LOCKED",
                Duration.ofSeconds(30));
        if (acquired == null || !acquired) {
            // Another submit is processing. If already finished, return result; otherwise
            // conflict.
            QuizInstance latest = quizInstanceRepo.findById(instanceId).orElse(null);
            if (latest != null && (latest.isSubmitted() || latest.isTimedOut())) {
                log.info("Submit idempotent return after lock for instance {} (status={})", instanceId,
                        latest.getStatus());
                return mapResultBasedOnConfig(latest, latest.getStatus());
            }
            log.warn("Submit in progress: instance {} locked by another submit", instanceId);
            return QuizResultDetailResDTO.builder()
                    .quizInstanceId(instanceId)
                    .status(QuizInstanceStatus.IN_PROGRESS.name())
                    .build();
        }

        try {
            return processSubmission(instance, QuizInstanceStatus.SUBMITTED);
        } finally {
            stringRedisTemplate.delete(submitLockKey);
        }
    }

    /**
     * Core submission logic shared between manual submit and timeout.
     */
    private QuizResultDetailResDTO processSubmission(QuizInstance instance, QuizInstanceStatus finalStatus) {
        Long instanceId = instance.getId();

        // Atomic read & delete from Redis using Lua script
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(GET_AND_DELETE_LUA);
        script.setResultType(List.class);

        List<Object> raw = stringRedisTemplate.execute(
                script,
                Collections.singletonList(answersKey(instanceId)));

        Map<String, AnswerRecord> answerMap = new HashMap<>();

        if (raw != null) {
            for (int i = 0; i < raw.size(); i += 2) {
                String key = (String) raw.get(i);
                String value = (String) raw.get(i + 1);
                try {
                    answerMap.put(key, objectMapper.readValue(value, AnswerRecord.class));
                } catch (Exception e) {
                    log.warn("Failed to parse answer for question {}", key);
                }
            }
        }
        // FALLBACK nếu Redis bị mất data
        if (answerMap.isEmpty()) {
            log.warn("Redis answers empty for instance {}, fallback to DB", instanceId);

            List<QuizUserResponse> dbResponses = quizUserResponseRepo.findByQuizInstanceId(instanceId);

            for (QuizUserResponse r : dbResponses) {
                if (r.getSelectedAnswerIds() != null) {
                    AnswerRecord record = new AnswerRecord();
                    record.setAnswer(
                            r.getSelectedAnswerIds().stream()
                                    .map(Long::intValue)
                                    .collect(Collectors.toList()));
                    if (r.getAnsweredAt() != null) {
                        record.setAnsweredAt(r.getAnsweredAt().atZone(ZoneId.systemDefault()).toInstant());
                    } else {
                        record.setAnsweredAt(Instant.now());
                    }
                    answerMap.put(r.getQuestionSnapshotKey(), record);
                }
            }
        }
        Map<String, QuizUserResponse> existingResponses = quizUserResponseRepo.findByQuizInstanceId(instanceId).stream()
                .collect(Collectors.toMap(QuizUserResponse::getQuestionSnapshotKey, r -> r, (a, b) -> a));
        QuizQuestionSnapshot snapshot = instance.getSnapshot();
        Long earnedPoints = 0L;
        List<QuizUserResponse> responsesToSave = new ArrayList<>();

        Map<String, Object> answersSnapshotMap = new HashMap<>();

        for (QuestionSnapshot qs : snapshot.getQuestions()) {
            String questionSnapshotKey = qs.getKey();
            AnswerRecord userRecord = answerMap.get(questionSnapshotKey);

            boolean isCorrect = false;
            List<Integer> userAnswer = (userRecord != null) ? userRecord.getAnswer() : null;
            if (userAnswer != null) {
                Set<String> userSet = userAnswer.stream().map(String::valueOf).collect(Collectors.toSet());
                Set<String> correctSet = new HashSet<>(qs.getCorrectAnswerIds());
                isCorrect = userSet.equals(correctSet);
                answersSnapshotMap.put(questionSnapshotKey, userAnswer);
            }

            List<Long> selectedAnswerIds = userAnswer != null
                    ? userAnswer.stream().map(Integer::longValue).collect(Collectors.toList())
                    : new ArrayList<>();

            QuizUserResponse response = existingResponses.get(questionSnapshotKey);
            if (response == null) {
                response = new QuizUserResponse();
            }
            response.setQuizInstance(instance);
            response.setQuestionId(null);
            response.setQuestionSnapshotKey(questionSnapshotKey);
            response.setSelectedAnswerIds(selectedAnswerIds);
            response.setSelectedAnswerId(null);
            response.setCorrect(isCorrect);
            response.setPointsEarned(isCorrect ? qs.getPoints() : 0);
            Instant answeredAt = userRecord != null ? userRecord.getAnsweredAt() : null;
            response.setAnsweredAt(
                    answeredAt != null ? LocalDateTime.ofInstant(answeredAt, ZoneId.systemDefault())
                            : LocalDateTime.now());
            response.setSkipped(userAnswer == null || userAnswer.isEmpty());

            responsesToSave.add(response);

            if (isCorrect)
                earnedPoints += qs.getPoints();
        }

        // Save all responses with retry on unique-constraint race
        try {
            quizUserResponseRepo.saveAll(responsesToSave);
        } catch (DataIntegrityViolationException e) {
            log.warn("Retry saveAll due to constraint violation for instance {}", instanceId, e);
            entityManager.clear();
            Map<String, QuizUserResponse> latestResponses = quizUserResponseRepo.findByQuizInstanceId(instanceId)
                    .stream()
                    .collect(Collectors.toMap(QuizUserResponse::getQuestionSnapshotKey, r -> r, (a, b) -> a));
            for (QuizUserResponse response : responsesToSave) {
                if (response.getId() == null) {
                    QuizUserResponse existing = latestResponses.get(response.getQuestionSnapshotKey());
                    if (existing != null) {
                        response.setId(existing.getId());
                    }
                }
            }
            quizUserResponseRepo.saveAll(responsesToSave);
        }
        // 6. Update instance
        instance.setEarnedPoints(earnedPoints);
        instance.setStatus(finalStatus);
        instance.setCorrectAnswers(responsesToSave.stream().filter(QuizUserResponse::isCorrect).count());
        instance.setEndedAt(LocalDateTime.now());
        instance.setAnswersSnapshot(answersSnapshotMap);
        instance = quizInstanceRepo.save(instance);

        applicationEvent.publishEvent(new QuizSubmissionEvent(instanceId));
        stringRedisTemplate.opsForZSet().remove(ACTIVE_INSTANCES_KEY, instanceId.toString());
        return mapResultBasedOnConfig(instance, finalStatus);
    }

    private QuizResultDetailResDTO mapResultBasedOnConfig(QuizInstance instance, QuizInstanceStatus status) {
        boolean showResult = instance.getQuiz().getConfig() != null
                && Boolean.TRUE.equals(instance.getQuiz().getConfig().getShowScoreImmediately());

        if (showResult || status == QuizInstanceStatus.SUBMITTED) {
            return mapToQuizResultDetailResDTO(instance);
        }
        return QuizResultDetailResDTO.builder().quizInstanceId(instance.getId()).status(status.name()).build();
    }

    // ============ 3.5 Timeout Handler ============
    @Override
    @Transactional
    public void handleTimedOutInstances() {
        // #13: Loop until no more expired instances remain
        Set<String> instanceIds;
        do {
            long nowEpoch = Instant.now().getEpochSecond();

            // 1. Get instances from Sorted Set where score (end_time) <= now, limit 100
            instanceIds = stringRedisTemplate.opsForZSet()
                    .rangeByScore(ACTIVE_INSTANCES_KEY, 0, nowEpoch, 0, 100);

            if (instanceIds == null || instanceIds.isEmpty()) {
                break;
            }

            for (String idStr : instanceIds) {
                Long instanceId = Long.parseLong(idStr);
                String lock = lockKey(instanceId);

                // 2. Try distributed lock
                Boolean locked = stringRedisTemplate.opsForValue()
                        .setIfAbsent(lock, "1", Duration.ofSeconds(60));

                if (locked == null || !locked) {
                    continue; // Another worker is processing this instance
                }

                try {
                    // 3. Verify in DB
                    QuizInstance instance = quizInstanceRepo.findById(instanceId).orElse(null);
                    if (instance == null || !instance.isInProgress()) {
                        // Already processed, just clean up Sorted Set
                        stringRedisTemplate.opsForZSet().remove(ACTIVE_INSTANCES_KEY, idStr);
                        continue;
                    }

                    // 4. Process as timeout (same as submit but with TIMED_OUT status)
                    log.info("Processing timeout for quiz instance {}", instanceId);
                    processSubmission(instance, QuizInstanceStatus.TIMED_OUT);

                } catch (Exception e) {
                    log.error("Error processing timeout for instance {}", instanceId, e);
                } finally {
                    // 5. Release lock
                    stringRedisTemplate.delete(lock);
                }
            }
        } while (instanceIds != null && !instanceIds.isEmpty());
    }

    // Listener xử lý xóa cache sau khi commit thành công
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCleanupAfterCommit(QuizSubmissionEvent event) {
        Long id = event.getInstanceId();
        // 5. Chỉ xóa Snapshot và Lock, giữ lại Answers (để debug/an toàn)
        stringRedisTemplate.delete(Arrays.asList(snapshotKey(id), lockKey(id)));
        stringRedisTemplate.opsForZSet().remove(ACTIVE_INSTANCES_KEY, id.toString());
        log.info("Cleanup completed for instance {}", id);
    }

    // ============ 2. Bổ sung Autosave định kỳ (UPSERT) ============

    @Scheduled(fixedRate = 30000) // Chạy mỗi 30 giây
    @Transactional
    public void handleAutosave() {
        // Chỉ lấy các instance đang làm bài
        List<QuizInstance> activeInstances = quizInstanceRepo.findByStatus(QuizInstanceStatus.IN_PROGRESS);

        for (QuizInstance instance : activeInstances) {
            String submitLockKey = "quiz:instance:" + instance.getId() + ":submit-lock";
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(submitLockKey))) {
                continue; // Skip autosave while submit is in progress
            }
            QuizQuestionSnapshot snapshot = getSnapshotFromCacheOrDb(instance);
            Map<String, QuestionSnapshot> questionSnapshotMap = snapshot != null && snapshot.getQuestions() != null
                    ? snapshot.getQuestions().stream().collect(Collectors.toMap(QuestionSnapshot::getKey, q -> q))
                    : new HashMap<>();
            Map<Object, Object> redisAnswers = stringRedisTemplate.opsForHash().entries(answersKey(instance.getId()));
            if (redisAnswers.isEmpty())
                continue;

            // Lấy các response hiện có trong DB để so sánh
            Map<String, QuizUserResponse> existingDBResponses = quizUserResponseRepo
                    .findByQuizInstanceId(instance.getId())
                    .stream()
                    .collect(Collectors.toMap(QuizUserResponse::getQuestionSnapshotKey, r -> r, (a, b) -> a));

            List<QuizUserResponse> updates = new ArrayList<>();

            for (Map.Entry<Object, Object> entry : redisAnswers.entrySet()) {
                String snapshotKey = (String) entry.getKey();
                QuestionSnapshot qs = questionSnapshotMap.get(snapshotKey);
                if (qs == null) {
                    log.warn("Snapshot question not found for key {} in instance {}", snapshotKey, instance.getId());
                    continue;
                }
                AnswerRecord record = parseRecord((String) entry.getValue());

                QuizUserResponse existing = existingDBResponses.get(snapshotKey);
                LocalDateTime redisAnsweredAt = LocalDateTime.ofInstant(record.getAnsweredAt(), ZoneId.systemDefault());

                // Chỉ UPSERT nếu là câu trả lời mới hoặc thời gian answeredAt mới hơn trong DB
                if (existing == null || existing.getAnsweredAt().isBefore(redisAnsweredAt)) {
                    QuizUserResponse response = existing != null ? existing : new QuizUserResponse();
                    response.setQuizInstance(instance);
                    response.setQuestionId(null);
                    response.setQuestionSnapshotKey(snapshotKey);
                    response.setAnsweredAt(redisAnsweredAt);
                    response.setSkipped(record.getAnswer().isEmpty());
                    response.setSelectedAnswerIds(
                            record.getAnswer().stream()
                                    .map(Integer::longValue)
                                    .collect(Collectors.toList()));
                    // Lưu ý: Autosave không cần tính điểm ngay để tiết kiệm CPU, việc tính điểm để
                    // lúc Submit
                    updates.add(response);
                }
            }

            if (!updates.isEmpty()) {
                quizUserResponseRepo.saveAll(updates);
                log.debug("Autosaved {} answers for instance {}", updates.size(), instance.getId());
            }
        }
    }

    // ============ Helper methods ============

    private AnswerRecord parseRecord(String json) {
        try {
            return objectMapper.readValue(json, AnswerRecord.class);
        } catch (Exception e) {
            return new AnswerRecord();
        }
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class QuizSubmissionEvent {
        private Long instanceId;
    }
    // ============ Other endpoints ============

    @Override
    public QuizInstanceResDTO getQuizInstance(Long instanceId, IdentityContext identity) {
        IdentityContext ctx = requireIdentity(identity);
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        assertOwnership(instance, ctx, "access this quiz instance");

        if (!instance.isInProgress()) {
            throw new ApiException(AppCode.FORBIDDEN, "Quiz instance is no longer in progress");
        }

        return quizInstanceMapper.toQuizInstanceResDTO(instance);
    }

    @Override
    @Transactional
    public void deleteQuizInstance(Long instanceId, IdentityContext identity) {
        IdentityContext ctx = requireIdentity(identity);
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        assertOwnership(instance, ctx, "delete this quiz instance");

        // Clean up Redis
        stringRedisTemplate.delete(answersKey(instanceId));
        stringRedisTemplate.delete(snapshotKey(instanceId));
        stringRedisTemplate.opsForZSet().remove(ACTIVE_INSTANCES_KEY, instanceId.toString());

        quizInstanceRepo.delete(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizInstance> instances = quizInstanceRepo.findAll(pageable);
        return convertToPageResDTO.convertPageResponse(instances, pageable,
                quizInstanceMapper::toQuizInstanceResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstancesByLobbyId(Long lobbyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizInstance> instances = quizInstanceRepo.findByQuiz_LobbyIdAndStatusIn(
                lobbyId,
                List.of(QuizInstanceStatus.SUBMITTED, QuizInstanceStatus.TIMED_OUT),
                pageable);
        return convertToPageResDTO.convertPageResponse(instances, pageable,
                quizInstanceMapper::toQuizInstanceResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstancesByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizInstance> instances = quizInstanceRepo.findByUserIdAndStatus(
                userId, QuizInstanceStatus.SUBMITTED, pageable);
        return convertToPageResDTO.convertPageResponse(instances, pageable,
                quizInstanceMapper::toQuizInstanceResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstancesByQuizId(Long quizId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizInstance> instances = quizInstanceRepo.findByQuizIdAndStatus(
                quizId, QuizInstanceStatus.SUBMITTED, pageable);
        return convertToPageResDTO.convertPageResponse(instances, pageable,
                quizInstanceMapper::toQuizInstanceResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstancesByQuizIdAndUserId(Long quizId, Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizInstance> instances = quizInstanceRepo.findByQuizIdAndUserIdAndStatusIn(
                quizId, userId,
                List.of(QuizInstanceStatus.SUBMITTED, QuizInstanceStatus.TIMED_OUT),
                pageable);
        return convertToPageResDTO.convertPageResponse(instances, pageable,
                quizInstanceMapper::toQuizInstanceResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserStartQuiz(Long quizId, IdentityContext identity) {
        IdentityContext ctx = requireIdentity(identity);
        Quiz quiz = quizRepo.findById(quizId).orElse(null);
        if (quiz == null || !quiz.isActive()) {
            return false;
        }
        // #3: Count both SUBMITTED and TIMED_OUT, #4: null guard
        if (quiz.getMaxAttempt() == null || quiz.getMaxAttempt() <= 0) {
            return true; // unlimited attempts
        }
        long attempts = countAttempts(quizId, ctx);
        return attempts < quiz.getMaxAttempt();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultDetailResDTO getQuizResult(Long instanceId, IdentityContext identity) {
        IdentityContext ctx = requireIdentity(identity);
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));
        assertOwnership(instance, ctx, "view this quiz result");

        // #10: Check that instance is completed
        if (instance.isInProgress()) {
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz is still in progress. Submit first to view results.");
        }

        // #10: Respect showScoreImmediately config
        boolean showResult = instance.getQuiz().getConfig() != null
                && Boolean.TRUE.equals(instance.getQuiz().getConfig().getShowScoreImmediately());
        if (!showResult && instance.getStatus() == QuizInstanceStatus.TIMED_OUT) {
            return QuizResultDetailResDTO.builder()
                    .quizInstanceId(instance.getId())
                    .status(instance.getStatus().name())
                    .build();
        }

        return mapToQuizResultDetailResDTO(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultDetailResDTO getQuizResultForHost(Long instanceId) {
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        if (instance.isInProgress()) {
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz is still in progress. Submit first to view results.");
        }

        boolean showResult = instance.getQuiz().getConfig() != null
                && Boolean.TRUE.equals(instance.getQuiz().getConfig().getShowScoreImmediately());
        if (!showResult && instance.getStatus() == QuizInstanceStatus.TIMED_OUT) {
            return QuizResultDetailResDTO.builder()
                    .quizInstanceId(instance.getId())
                    .status(instance.getStatus().name())
                    .build();
        }

        return mapToQuizResultDetailResDTO(instance);
    }

    // ============ Helper methods ============

    private QuizQuestionSnapshot getSnapshotFromCacheOrDb(QuizInstance instance) {
        // Try Redis cache first
        String cachedSnapshot = stringRedisTemplate.opsForValue().get(snapshotKey(instance.getId()));
        if (cachedSnapshot != null) {
            try {
                return objectMapper.readValue(cachedSnapshot, QuizQuestionSnapshot.class);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse cached snapshot for instance {}", instance.getId());
            }
        }

        // Fallback to DB
        QuizQuestionSnapshot snapshot = instance.getSnapshot();

        // Re-cache if instance is still in progress
        if (snapshot != null && instance.isInProgress() && snapshot.getDuration() != null) {
            try {
                long ttlSeconds = (long) snapshot.getDuration() * 60 + 3600;
                stringRedisTemplate.opsForValue().set(
                        snapshotKey(instance.getId()),
                        objectMapper.writeValueAsString(snapshot),
                        Duration.ofSeconds(ttlSeconds));
            } catch (JsonProcessingException e) {
                log.warn("Failed to re-cache snapshot for instance {}", instance.getId());
            }
        }

        return snapshot;
    }

    private QuizResultDetailResDTO mapToQuizResultDetailResDTO(QuizInstance instance) {
        QuizQuestionSnapshot snapshot = instance.getSnapshot();
        List<QuestionResultResDTO> questionResults = new ArrayList<>();

        Map<String, QuizUserResponse> responses = new ArrayList<>(
                quizUserResponseRepo.findByQuizInstanceId(instance.getId())).stream()
                .collect(Collectors.toMap(QuizUserResponse::getQuestionSnapshotKey, r -> r, (a, b) -> a));

        if (snapshot != null && snapshot.getQuestions() != null) {
            for (QuestionSnapshot qs : snapshot.getQuestions()) {
                QuizUserResponse userResponse = responses.get(qs.getKey());
                questionResults.add(mapQuestionResultResDTO(qs, userResponse));
            }
        }

        return QuizResultDetailResDTO.builder()
                .quizInstanceId(instance.getId())
                .quizId(instance.getQuiz().getId())
                .quizTitle(instance.getQuiz().getTitle())
                .userId(instance.getUser() != null ? instance.getUser().getId() : null)
                .userName(instance.getUser() != null ? instance.getUser().getUserName() : null)
                .status(instance.getStatus() != null ? instance.getStatus().name() : null)
                .scorePercentage(instance.getScorePercentage())
                .totalTimeSpentMinutes(instance.getElapsedTimeSeconds())
                .totalPoints(instance.getTotalPoints())
                .earnedPoints(instance.getEarnedPoints())
                .questionResults(questionResults)
                .build();
    }

    private QuestionResultResDTO mapQuestionResultResDTO(QuestionSnapshot qs, QuizUserResponse userResponse) {
        List<AnswerResultResDTO> answerResults = new ArrayList<>();
        Set<String> correctSet = new HashSet<>(qs.getCorrectAnswerIds());
        Set<Integer> userSet = userResponse != null && userResponse.getSelectedAnswerIds() != null
                ? userResponse.getSelectedAnswerIds().stream().map(Long::intValue).collect(Collectors.toSet())
                : new HashSet<>();

        List<String> correctAnswerTexts = new ArrayList<>();
        List<String> userAnswerTexts = new ArrayList<>();

        for (int i = 0; i < qs.getAnswers().size(); i++) {
            AnswerSnapshot as = qs.getAnswers().get(i);
            boolean isCorrect = correctSet.contains(String.valueOf(i));
            boolean isUserSelected = userSet.contains(i);

            if (isCorrect) {
                correctAnswerTexts.add(as.getContent());
            }
            if (isUserSelected) {
                userAnswerTexts.add(as.getContent());
            }

            answerResults.add(AnswerResultResDTO.builder()
                    .answerInstanceId((long) i)
                    .displayOrder(i + 1)
                    .answerText(as.getContent())
                    .optionLabel(String.valueOf((char) ('A' + i)))
                    .isCorrect(isCorrect)
                    .isUserSelected(isUserSelected)
                    .build());
        }

        return QuestionResultResDTO.builder()
                .questionInstanceId(null)
                .displayOrder(qs.getOrderIndex())
                .questionText(qs.getContent())
                .type(qs.getType() != null ? qs.getType().name() : null)
                .answerType(qs.getAnswerType() != null ? qs.getAnswerType().name() : null)
                .points(qs.getPoints())
                .earnedPoints(userResponse != null ? userResponse.getPointsEarned() : 0L)
                .userAnswer(userAnswerTexts.isEmpty() ? null : String.join(", ", userAnswerTexts))
                .correctAnswer(correctAnswerTexts.isEmpty() ? null : String.join(", ", correctAnswerTexts))
                .isCorrect(userResponse != null && userResponse.isCorrect())
                .isSkipped(userResponse != null && userResponse.isSkipped())
                .status(userResponse != null ? userResponse.getStatus().name() : ResponseStatus.SKIPPED.name())
                .allAnswers(answerResults)
                .build();
    }
}
