package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.AnswerSubmissionDTO;
import com.tomzxy.web_quiz.dto.requests.CreateAttemptReqDTO;
import com.tomzxy.web_quiz.dto.responses.*;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.QuizUserResponseRepo;
import com.tomzxy.web_quiz.services.AttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttemptServiceImpl implements AttemptService {

    private final QuizInstanceRepo quizInstanceRepo;
    private final QuizUserResponseRepo quizUserResponseRepo;
    private final QuizRepo quizRepo;

    @Override
    public List<AttemptResDTO> getAllAttempts(Long userId, Long quizId) {
        List<QuizInstance> instances;
        if (userId != null && quizId != null) {
            instances = quizInstanceRepo.findAllByQuizIdAndUserIdAndStatus(
                    quizId, userId, QuizInstanceStatus.SUBMITTED);
        } else if (userId != null) {
            instances = quizInstanceRepo.findByUserIdAndStatus(userId, QuizInstanceStatus.SUBMITTED);
        } else if (quizId != null) {
            instances = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED);
        } else {
            instances = quizInstanceRepo.findByStatus(QuizInstanceStatus.SUBMITTED);
        }
        return mapToAttemptResDTOs(instances);
    }

    @Override
    public AttemptDetailResDTO getAttemptById(Long id) {
        QuizInstance instance = quizInstanceRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Attempt not found"));
        return mapToAttemptDetailResDTO(instance);
    }

    @Override
    public List<AttemptResDTO> getAttemptsByUser(Long userId) {
        List<QuizInstance> instances = quizInstanceRepo.findByUserIdAndStatus(userId, QuizInstanceStatus.SUBMITTED);
        return mapToAttemptResDTOs(instances);
    }

    @Override
    public List<AttemptResDTO> getAttemptsByQuizAndUser(Long quizId, Long userId) {
        List<QuizInstance> instances = quizInstanceRepo.findAllByQuizIdAndUserIdAndStatus(
                quizId, userId, QuizInstanceStatus.SUBMITTED);
        return mapToAttemptResDTOs(instances);
    }

    @Override
    public AttemptDetailResDTO createAttempt(CreateAttemptReqDTO request) {
        Quiz quiz = quizRepo.findById(request.getQuizId())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new ApiException(AppCode.BAD_REQUEST, "Answer list cannot be empty");
        }

        Map<Long, QuizQuestionLink> linkByQuestionId = quiz.getQuizQuestionLinks().stream()
                .collect(Collectors.toMap(
                        link -> link.getQuestion().getId(),
                        link -> link));
        
        // Note: userId should come from security context in real implementation
        // For now, we'll create instance without user - you'll need to inject IdentityResolver or SecurityContext
        // Long userId = identityResolver.getCurrentUserId(); // Example
        
        QuizInstance instance = QuizInstance.builder()
                .quiz(quiz)
                .user(null) // Set from security context in real implementation
                .startedAt(request.getStartedAt() != null ? request.getStartedAt() : LocalDateTime.now())
                .endedAt(request.getCompletedAt())
                .status(QuizInstanceStatus.SUBMITTED)
                .totalPoints(calculateTotalPoints(quiz))
                .earnedPoints(0L)
                .build();
        
        instance = quizInstanceRepo.save(instance);
        
        // Process answers
        Long earnedPoints = 0L;
        int correctAnswers = 0;
        Set<Long> seenQuestionIds = new HashSet<>();
        for (AnswerSubmissionDTO answerSubmission : request.getAnswers()) {
            Long questionId = answerSubmission.getQuestionId();
            if (questionId == null) {
                throw new ApiException(AppCode.BAD_REQUEST, "Question ID is required");
            }
            if (!seenQuestionIds.add(questionId)) {
                throw new ApiException(AppCode.BAD_REQUEST, "Duplicate question in request: " + questionId);
            }
            QuizQuestionLink link = linkByQuestionId.get(questionId);
            if (link == null) {
                throw new NotFoundException("Question not in quiz: " + questionId);
            }
            Question question = link.getQuestion();
            
            boolean isCorrect = checkAnswer(question, answerSubmission);
            Long points = link.getPoints() != null ? link.getPoints() : 1L;
            
            if (isCorrect) {
                earnedPoints += points;
                correctAnswers++;
            }
            
            QuizUserResponse response = QuizUserResponse.builder()
                    .quizInstance(instance)
                    .questionId(question.getId())
                    .selectedAnswerId(answerSubmission.getSelectedOptionIds() != null && !answerSubmission.getSelectedOptionIds().isEmpty() 
                            ? answerSubmission.getSelectedOptionIds().get(0) : null)
                    .isCorrect(isCorrect)
                    .pointsEarned(isCorrect ? points : 0)
                    .answeredAt(LocalDateTime.now())
                    .isSkipped(false)
                    .build();
            
            quizUserResponseRepo.save(response);
        }
        
        instance.setEarnedPoints(earnedPoints);
        instance.setEndedAt(request.getCompletedAt() != null ? request.getCompletedAt() : LocalDateTime.now());
        instance = quizInstanceRepo.save(instance);
        
        return mapToAttemptDetailResDTO(instance);
    }

    @Override
    public void deleteAttempt(Long id) {
        QuizInstance instance = quizInstanceRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Attempt not found"));
        quizInstanceRepo.delete(instance);
    }

    @Override
    public UserStatisticsResDTO getUserStatistics(Long userId) {
        List<QuizInstance> instances = quizInstanceRepo.findByUserIdAndStatus(userId, QuizInstanceStatus.SUBMITTED);
        
        int totalQuizzesTaken = instances.size();
        Long totalPoints = instances.stream()
                .mapToLong(i -> i.getEarnedPoints() != null ? i.getEarnedPoints() : 0L)
                .sum();
        double averageScore = instances.stream()
                .mapToDouble(QuizInstance::getScorePercentage)
                .average()
                .orElse(0.0);
        int totalTimeSpent = instances.stream()
                .mapToInt(i -> {
                    Long seconds = i.getElapsedTimeSeconds();
                    return seconds != null ? seconds.intValue() : 0;
                })
                .sum();
        
        Map<String, Integer> quizzesBySubject = instances.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getQuiz().getSubject().getSubjectName(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        
        Map<String, Integer> quizzesByDifficulty = new HashMap<>(); // Would need difficulty field
        
        List<QuizInstance> recentActivityInstances = instances.stream()
                .sorted(Comparator.comparing(QuizInstance::getEndedAt).reversed())
                .limit(10)
                .collect(Collectors.toList());
        List<AttemptResDTO> recentActivityDtos = mapToAttemptResDTOs(recentActivityInstances);
        
        return UserStatisticsResDTO.builder()
                .userId(userId)
                .totalQuizzesTaken(totalQuizzesTaken)
                .totalPoints(totalPoints)
                .averageScore(averageScore)
                .totalTimeSpent(totalTimeSpent)
                .quizzesBySubject(quizzesBySubject)
                .quizzesByDifficulty(quizzesByDifficulty)
                .recentActivity(recentActivityDtos)
                .build();
    }

    private List<AttemptResDTO> mapToAttemptResDTOs(List<QuizInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> instanceIds = instances.stream()
                .map(QuizInstance::getId)
                .toList();
        Map<Long, Long> correctCountByInstanceId = quizUserResponseRepo
                .countCorrectByQuizInstanceIdIn(instanceIds)
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]));

        return instances.stream()
                .map(instance -> mapToAttemptResDTO(
                        instance,
                        correctCountByInstanceId.getOrDefault(instance.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private AttemptResDTO mapToAttemptResDTO(QuizInstance instance, long correctAnswers) {
        return AttemptResDTO.builder()
                .id(instance.getId())
                .quizId(instance.getQuiz().getId())
                .userId(instance.getUser() != null ? instance.getUser().getId() : null)
                .title(instance.getQuiz().getTitle())
                .date(formatDate(instance.getEndedAt() != null ? instance.getEndedAt() : instance.getStartedAt()))
                .score(formatScore(instance.getScorePercentage()))
                .totalQuestions(instance.getQuiz().getTotalQuestion() != null ? instance.getQuiz().getTotalQuestion().intValue() : 0)
                .correctAnswers((int) correctAnswers)
                .points(instance.getEarnedPoints())
                .duration(formatDuration(instance.getStartedAt(), instance.getEndedAt()))
                .completedAt(instance.getEndedAt())
                .badges(calculateBadges(instance))
                .badgeColors(calculateBadgeColors(instance))
                .build();
    }

    private AttemptDetailResDTO mapToAttemptDetailResDTO(QuizInstance instance) {
        List<QuizUserResponse> responses = quizUserResponseRepo.findByQuizInstanceId(instance.getId());
        long correctAnswers = responses.stream()
                .filter(QuizUserResponse::isCorrect)
                .count();
        AttemptResDTO base = mapToAttemptResDTO(instance, correctAnswers);
        Map<Long, QuizUserResponse> responseByQuestionId = responses.stream()
                .collect(Collectors.toMap(
                        QuizUserResponse::getQuestionId,
                        r -> r,
                        (a, b) -> a));
        List<Question> questions = instance.getQuiz().getQuizQuestionLinks().stream()
                .map(QuizQuestionLink::getQuestion)
                .collect(Collectors.toList());
        
        List<UserAnswerResDTO> answers = new ArrayList<>();
        for (Question question : questions) {
            QuizUserResponse response = responseByQuestionId.get(question.getId());
            
            answers.add(UserAnswerResDTO.builder()
                    .id(response != null ? response.getId() : null)
                    .attemptId(instance.getId())
                    .questionId(question.getId())
                    .selectedOptionIds(response != null && response.getSelectedAnswerId() != null 
                            ? Collections.singletonList(response.getSelectedAnswerId()) : Collections.emptyList())
                    .isCorrect(response != null && response.isCorrect())
                    .pointsEarned(response != null ? response.getPointsEarned() : 0)
                    .build());
        }
        
        return AttemptDetailResDTO.builder()
                .id(base.getId())
                .quizId(base.getQuizId())
                .userId(base.getUserId())
                .title(base.getTitle())
                .date(base.getDate())
                .score(base.getScore())
                .totalQuestions(base.getTotalQuestions())
                .correctAnswers(base.getCorrectAnswers())
                .points(base.getPoints())
                .duration(base.getDuration())
                .completedAt(base.getCompletedAt())
                .badges(base.getBadges())
                .badgeColors(base.getBadgeColors())
                .answers(answers)
                .build();
    }

    private boolean checkAnswer(Question question, AnswerSubmissionDTO submission) {
        Set<Long> allAnswerIds = question.getAnswers().stream()
                .map(Answer::getId)
                .collect(Collectors.toSet());
        List<Answer> correctAnswers = question.getAnswers().stream()
                .filter(Answer::isAnswerCorrect)
                .collect(Collectors.toList());
        
        if (submission.getSelectedOptionIds() != null && !submission.getSelectedOptionIds().isEmpty()) {
            Set<Long> selected = new HashSet<>(submission.getSelectedOptionIds());
            if (!allAnswerIds.containsAll(selected)) {
                return false;
            }
            Set<Long> correctIds = correctAnswers.stream()
                    .map(Answer::getId)
                    .collect(Collectors.toSet());
            return !correctIds.isEmpty() && selected.equals(correctIds);
        }
        
        if (submission.getAnswerText() != null && !submission.getAnswerText().trim().isEmpty()) {
            return correctAnswers.stream()
                    .anyMatch(ca -> ca.getAnswerName().equalsIgnoreCase(submission.getAnswerText().trim()));
        }
        
        return false;
    }

    private Long calculateTotalPoints(Quiz quiz) {
        return quiz.getQuizQuestionLinks().stream()
                .mapToLong(QuizQuestionLink::getPoints)
                .sum();
    }

    private String formatScore(Double scorePercentage) {
        if (scorePercentage == null) return "0%";
        return String.format("%.0f%%", scorePercentage);
    }

    private String formatDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "0m";
        Duration duration = Duration.between(start, end);
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private List<String> calculateBadges(QuizInstance instance) {
        List<String> badges = new ArrayList<>();
        double score = instance.getScorePercentage();
        if (score >= 90) badges.add("Excellent");
        else if (score >= 80) badges.add("Great");
        else if (score >= 70) badges.add("Good");
        else if (score >= 60) badges.add("Pass");
        return badges;
    }

    private List<String> calculateBadgeColors(QuizInstance instance) {
        List<String> colors = new ArrayList<>();
        double score = instance.getScorePercentage();
        if (score >= 90) colors.add("#4CAF50");
        else if (score >= 80) colors.add("#2196F3");
        else if (score >= 70) colors.add("#FF9800");
        else if (score >= 60) colors.add("#9E9E9E");
        return colors;
    }
}
