package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizSubmissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.*;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionResultResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.enums.QuizVisibility;
import com.tomzxy.web_quiz.enums.ResponseStatus;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import com.tomzxy.web_quiz.mapstructs.QuizInstanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizInstanceServiceImpl implements QuizInstanceService {

    private final QuizInstanceRepo quizInstanceRepo;
    private final QuizUserResponseRepo quizUserResponseRepo;
    private final QuizRepo quizRepo;
    private final UserRepo userRepo;
    private final QuestionRepo questionRepo;
    private final QuizInstanceMapper quizInstanceMapper;
    private final ConvertToPageResDTO convertToPageResDTO;

    @Override
    public QuizInstanceResDTO createQuizInstance(QuizInstanceReqDTO request) {
        log.info("Creating quiz instance for quiz: {}", request.getQuizId());

        Quiz quiz = quizRepo.findById(request.getQuizId())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        // Check quiz visibility – only PUBLIC quizzes for now
        if (quiz.getVisibility() == null || !quiz.getVisibility().equals(QuizVisibility.PUBLIC)) {
            throw new ApiException(AppCode.FORBIDDEN, "User is not allowed to join this quiz");
        }

        // Shuffle config will be used when snapshot-based exam flow is implemented
        // boolean shuffleQuestions = quiz.getConfig() != null &&
        // Boolean.TRUE.equals(quiz.getConfig().getShuffleQuestions());
        // boolean shuffleAnswers = quiz.getConfig() != null &&
        // Boolean.TRUE.equals(quiz.getConfig().getShuffleAnswers());

        // Calculate total points from quiz question links
        int totalPoints = quiz.getQuizQuestionLinks().stream()
                .mapToInt(QuizQuestionLink::getPoints)
                .sum();

        QuizInstance instance = QuizInstance.builder()
                .quiz(quiz)
                .user(null) // Set from auth context when security is implemented
                .startedAt(LocalDateTime.now())
                .status(QuizInstanceStatus.IN_PROGRESS)
                .totalPoints(totalPoints)
                .earnedPoints(0)
                .build();

        instance = quizInstanceRepo.save(instance);

        return quizInstanceMapper.toQuizInstanceResDTO(instance);
    }

    @Override
    public QuizInstanceResDTO getQuizInstance(Long instanceId, Long userId) {
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        if (instance.getUser() != null && !instance.getUser().getId().equals(userId)) {
            throw new ApiException(AppCode.FORBIDDEN);
        }

        if (!instance.isInProgress()) {
            throw new ApiException(AppCode.FORBIDDEN, "Quiz instance is no longer in progress");
        }

        return quizInstanceMapper.toQuizInstanceResDTO(instance);
    }

    @Override
    public QuizResultDetailResDTO submitQuiz(QuizSubmissionReqDTO request) {
        QuizInstance instance = quizInstanceRepo.findById(request.getQuizInstanceId())
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        if (!instance.isInProgress()) {
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz instance is no longer in progress");
        }

        // Process answers
        int earnedPoints = 0;
        Map<Long, String> answers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();
        Map<Long, Long> answerIds = request.getAnswerIds() != null ? request.getAnswerIds() : Collections.emptyMap();

        List<Question> questions = getQuestionsFromQuiz(instance.getQuiz());

        for (Question question : questions) {
            String selectedText = answers.get(question.getId());
            Long selectedAnswerId = answerIds.get(question.getId());

            boolean isCorrect = false;
            boolean isSkipped = (selectedText == null || selectedText.isBlank()) && selectedAnswerId == null;

            if (!isSkipped) {
                // Check correctness
                Answer correctAnswer = question.getAnswers().stream()
                        .filter(Answer::isAnswerCorrect)
                        .findFirst()
                        .orElse(null);

                if (selectedAnswerId != null && correctAnswer != null) {
                    isCorrect = correctAnswer.getId().equals(selectedAnswerId);
                } else if (selectedText != null && correctAnswer != null) {
                    isCorrect = correctAnswer.getAnswerName().equalsIgnoreCase(selectedText.trim());
                }
            }

            Integer points = getQuestionPoints(instance.getQuiz(), question);

            QuizUserResponse response = QuizUserResponse.builder()
                    .quizInstance(instance)
                    .questionId(question.getId())
                    .selectedAnswerId(selectedAnswerId)
                    .selectedAnswerText(selectedText)
                    .isCorrect(isCorrect)
                    .pointsEarned(isCorrect ? points : 0)
                    .answeredAt(LocalDateTime.now())
                    .isSkipped(isSkipped)
                    .build();

            quizUserResponseRepo.save(response);

            if (isCorrect) {
                earnedPoints += points;
            }
        }

        // Update instance
        instance.setEarnedPoints(earnedPoints);
        instance.setStatus(QuizInstanceStatus.SUBMITTED);
        instance.setEndedAt(request.getSubmittedAt() != null ? request.getSubmittedAt() : LocalDateTime.now());
        instance = quizInstanceRepo.save(instance);

        return mapToQuizResultDetailResDTO(instance);
    }

    @Override
    public void handleTimedOutInstances() {
        List<QuizInstance> timedOutInstances = quizInstanceRepo.findTimedOutInstances(
                LocalDateTime.now(), QuizInstanceStatus.IN_PROGRESS.name());

        for (QuizInstance instance : timedOutInstances) {
            instance.markAsTimedOut();
            quizInstanceRepo.save(instance);
            log.info("Quiz instance {} timed out", instance.getId());
        }
    }

    @Override
    public void deleteQuizInstance(Long instanceId, Long userId) {
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        if (instance.getUser() != null && !instance.getUser().getId().equals(userId)) {
            throw new ApiException(AppCode.NOT_PERMISSION, "No permission to delete this quiz instance");
        }

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
        // Get quiz instances where quiz belongs to the lobby
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizInstance> instances = quizInstanceRepo.findAll(pageable);
        // Filter by lobbyId in memory (could be optimized with a custom query)
        List<?> filtered = instances.getContent().stream()
                .filter(i -> i.getQuiz().getLobby() != null && i.getQuiz().getLobby().getId().equals(lobbyId))
                .map(quizInstanceMapper::toQuizInstanceResDTO)
                .collect(Collectors.toList());
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items((List) filtered)
                .total((long) filtered.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstancesByUserId(Long userId, int page, int size) {
        List<QuizInstance> instances = quizInstanceRepo.findByUserIdAndStatus(userId, QuizInstanceStatus.SUBMITTED);
        List<?> dtos = instances.stream()
                .map(quizInstanceMapper::toQuizInstanceResDTO)
                .collect(Collectors.toList());
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items((List) dtos)
                .total((long) dtos.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstancesByQuizId(Long quizId, int page, int size) {
        List<QuizInstance> instances = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED);
        List<?> dtos = instances.stream()
                .map(quizInstanceMapper::toQuizInstanceResDTO)
                .collect(Collectors.toList());
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items((List) dtos)
                .total((long) dtos.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getAllQuizInstancesByQuizIdAndUserId(Long quizId, Long userId, int page, int size) {
        List<QuizInstance> instances = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED)
                .stream()
                .filter(i -> i.getUser() != null && i.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        List<?> dtos = instances.stream()
                .map(quizInstanceMapper::toQuizInstanceResDTO)
                .collect(Collectors.toList());
        return PageResDTO.builder()
                .page(page)
                .size(size)
                .items((List) dtos)
                .total((long) dtos.size())
                .build();
    }

    @Override
    public boolean canUserStartQuiz(Long quizId, Long userId) {
        Quiz quiz = quizRepo.findById(quizId).orElse(null);
        if (quiz == null || !quiz.isActive()) {
            return false;
        }
        long userAttempts = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED)
                .stream()
                .filter(i -> i.getUser() != null && i.getUser().getId().equals(userId))
                .count();
        return userAttempts < quiz.getMaxAttempt();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultDetailResDTO getQuizResult(Long instanceId, Long userId) {
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));
        if (instance.getUser() != null && !instance.getUser().getId().equals(userId)) {
            throw new ApiException(AppCode.NOT_PERMISSION, "No permission to view this quiz result");
        }
        return mapToQuizResultDetailResDTO(instance);
    }

    // Helper methods

    private List<Question> getQuestionsFromQuiz(Quiz quiz) {
        return quiz.getQuizQuestionLinks().stream()
                .map(QuizQuestionLink::getQuestion)
                .collect(Collectors.toList());
    }

    private Integer getQuestionPoints(Quiz quiz, Question question) {
        return quiz.getQuizQuestionLinks().stream()
                .filter(link -> link.getQuestion().getId().equals(question.getId()))
                .map(QuizQuestionLink::getPoints)
                .findFirst()
                .orElse(1);
    }

    private QuizResultDetailResDTO mapToQuizResultDetailResDTO(QuizInstance instance) {
        List<Question> questions = getQuestionsFromQuiz(instance.getQuiz());
        List<QuestionResultResDTO> questionResults = new ArrayList<>();
        List<QuizUserResponse> responses = new ArrayList<>(
                quizUserResponseRepo.findByQuizInstanceId(instance.getId()));

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            QuizUserResponse userResponse = responses.stream()
                    .filter(r -> question.getId().equals(r.getQuestionId()))
                    .findFirst()
                    .orElse(null);
            questionResults.add(mapQuestionResultResDTO(question, instance, userResponse, i + 1));
        }

        return QuizResultDetailResDTO.builder()
                .quizInstanceId(instance.getId())
                .quizId(instance.getQuiz().getId())
                .quizTitle(instance.getQuiz().getTitle())
                .userId(instance.getUser() != null ? instance.getUser().getId() : null)
                .userName(instance.getUser() != null ? instance.getUser().getUserName() : null)
                .status(instance.getStatus() != null ? instance.getStatus().name() : null)
                .scorePercentage(instance.getScorePercentage())
                .totalTimeSpentMinutes(instance.getElapsedTimeMinutes())
                .totalPoints(instance.getTotalPoints())
                .earnedPoints(instance.getEarnedPoints())
                .questionResults(questionResults)
                .build();
    }

    private QuestionResultResDTO mapQuestionResultResDTO(Question question, QuizInstance instance,
            QuizUserResponse userResponse, int displayOrder) {
        List<Answer> allAnswers = new ArrayList<>(question.getAnswers());
        List<AnswerResultResDTO> answerResults = new ArrayList<>();

        for (int i = 0; i < allAnswers.size(); i++) {
            Answer answer = allAnswers.get(i);
            boolean isUserSelected = userResponse != null &&
                    answer.getId().equals(userResponse.getSelectedAnswerId());
            answerResults.add(AnswerResultResDTO.builder()
                    .answerInstanceId(answer.getId())
                    .displayOrder(i + 1)
                    .answerText(answer.getAnswerName())
                    .isCorrect(answer.isAnswerCorrect())
                    .isUserSelected(isUserSelected)
                    .build());
        }

        Answer correctAnswer = question.getAnswers().stream()
                .filter(Answer::isAnswerCorrect)
                .findFirst()
                .orElse(null);

        Integer points = getQuestionPoints(instance.getQuiz(), question);

        return QuestionResultResDTO.builder()
                .questionInstanceId(question.getId())
                .displayOrder(displayOrder)
                .questionText(question.getQuestionName())
                .questionType(question.getQuestionType().name())
                .points(points)
                .earnedPoints(userResponse != null ? userResponse.getPointsEarned() : 0)
                .userAnswer(userResponse != null ? userResponse.getSelectedAnswerText() : null)
                .correctAnswer(correctAnswer != null ? correctAnswer.getAnswerName() : null)
                .isCorrect(userResponse != null && userResponse.isCorrect())
                .isSkipped(userResponse != null && userResponse.isSkipped())
                .status(userResponse != null ? userResponse.getStatus().name() : ResponseStatus.SKIPPED.name())
                .allAnswers(answerResults)
                .build();
    }
}
