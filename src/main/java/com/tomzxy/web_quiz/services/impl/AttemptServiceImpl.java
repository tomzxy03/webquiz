package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.AnswerSubmissionDTO;
import com.tomzxy.web_quiz.dto.requests.CreateAttemptReqDTO;
import com.tomzxy.web_quiz.dto.responses.*;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;
import com.tomzxy.web_quiz.repositories.*;
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
    private final QuestionRepo questionRepo;

    @Override
    public List<AttemptResDTO> getAllAttempts(Long userId, Long quizId) {
        List<QuizInstance> instances;
        if (userId != null && quizId != null) {
            instances = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED)
                    .stream()
                    .filter(i -> i.getUser() != null && i.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        } else if (userId != null) {
            instances = quizInstanceRepo.findByUserIdAndStatus(userId, QuizInstanceStatus.SUBMITTED);
        } else if (quizId != null) {
            instances = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED);
        } else {
            instances = quizInstanceRepo.findByStatus(QuizInstanceStatus.SUBMITTED);
        }
        return instances.stream()
                .map(this::mapToAttemptResDTO)
                .collect(Collectors.toList());
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
        return instances.stream()
                .map(this::mapToAttemptResDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttemptResDTO> getAttemptsByQuizAndUser(Long quizId, Long userId) {
        List<QuizInstance> instances = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED)
                .stream()
                .filter(i -> i.getUser() != null && i.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        return instances.stream()
                .map(this::mapToAttemptResDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AttemptDetailResDTO createAttempt(CreateAttemptReqDTO request) {
        Quiz quiz = quizRepo.findById(request.getQuizId())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));
        
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
        for (AnswerSubmissionDTO answerSubmission : request.getAnswers()) {
            Question question = questionRepo.findById(answerSubmission.getQuestionId())
                    .orElseThrow(() -> new NotFoundException("Question not found: " + answerSubmission.getQuestionId()));
            
            boolean isCorrect = checkAnswer(question, answerSubmission);
            Long points = getQuestionPoints(quiz, question);
            
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
        
        List<AttemptResDTO> recentActivity = instances.stream()
                .sorted(Comparator.comparing(QuizInstance::getEndedAt).reversed())
                .limit(10)
                .map(this::mapToAttemptResDTO)
                .collect(Collectors.toList());
        
        return UserStatisticsResDTO.builder()
                .userId(userId)
                .totalQuizzesTaken(totalQuizzesTaken)
                .totalPoints(totalPoints)
                .averageScore(averageScore)
                .totalTimeSpent(totalTimeSpent)
                .quizzesBySubject(quizzesBySubject)
                .quizzesByDifficulty(quizzesByDifficulty)
                .recentActivity(recentActivity)
                .build();
    }

    private AttemptResDTO mapToAttemptResDTO(QuizInstance instance) {
        int correctAnswers = (int) quizUserResponseRepo.findByQuizInstanceId(instance.getId()).stream()
                .filter(QuizUserResponse::isCorrect)
                .count();
        
        return AttemptResDTO.builder()
                .id(instance.getId())
                .quizId(instance.getQuiz().getId())
                .userId(instance.getUser() != null ? instance.getUser().getId() : null)
                .title(instance.getQuiz().getTitle())
                .date(formatDate(instance.getEndedAt() != null ? instance.getEndedAt() : instance.getStartedAt()))
                .score(formatScore(instance.getScorePercentage()))
                .totalQuestions(instance.getQuiz().getTotalQuestions())
                .correctAnswers(correctAnswers)
                .points(instance.getEarnedPoints())
                .duration(formatDuration(instance.getStartedAt(), instance.getEndedAt()))
                .completedAt(instance.getEndedAt())
                .badges(calculateBadges(instance))
                .badgeColors(calculateBadgeColors(instance))
                .build();
    }

    private AttemptDetailResDTO mapToAttemptDetailResDTO(QuizInstance instance) {
        AttemptResDTO base = mapToAttemptResDTO(instance);
        
        List<QuizUserResponse> responses = quizUserResponseRepo.findByQuizInstanceId(instance.getId());
        List<Question> questions = instance.getQuiz().getQuizQuestionLinks().stream()
                .map(QuizQuestionLink::getQuestion)
                .collect(Collectors.toList());
        
        List<UserAnswerResDTO> answers = new ArrayList<>();
        for (Question question : questions) {
            QuizUserResponse response = responses.stream()
                    .filter(r -> r.getQuestionId().equals(question.getId()))
                    .findFirst()
                    .orElse(null);
            
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
        List<Answer> correctAnswers = question.getAnswers().stream()
                .filter(Answer::isAnswerCorrect)
                .collect(Collectors.toList());
        
        if (submission.getSelectedOptionIds() != null && !submission.getSelectedOptionIds().isEmpty()) {
            return correctAnswers.stream()
                    .anyMatch(ca -> submission.getSelectedOptionIds().contains(ca.getId()));
        }
        
        if (submission.getAnswerText() != null && !submission.getAnswerText().trim().isEmpty()) {
            return correctAnswers.stream()
                    .anyMatch(ca -> ca.getAnswerName().equalsIgnoreCase(submission.getAnswerText().trim()));
        }
        
        return false;
    }

    private Long getQuestionPoints(Quiz quiz, Question question) {
        return quiz.getQuizQuestionLinks().stream()
                .filter(link -> link.getQuestion().getId().equals(question.getId()))
                .map(QuizQuestionLink::getPoints)
                .findFirst()
                .orElse(1L); // Default to 1 point if not found
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
