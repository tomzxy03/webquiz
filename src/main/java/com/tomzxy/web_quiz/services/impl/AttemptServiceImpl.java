package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.UserAnswerResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.AttemptDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.AttemptResDTO;
import com.tomzxy.web_quiz.dto.responses.answer.AnswerResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.IdentityType;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.IdentityContext;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;
import com.tomzxy.web_quiz.models.snapshot.AnswerSnapshot;
import com.tomzxy.web_quiz.models.snapshot.QuestionSnapshot;
import com.tomzxy.web_quiz.models.snapshot.QuizQuestionSnapshot;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.QuizUserResponseRepo;
import com.tomzxy.web_quiz.services.AttemptService;
import com.tomzxy.web_quiz.services.common.ConvertToPageResDTO;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final QuizRepo quizRepo;
    private final QuizInstanceRepo quizInstanceRepo;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final QuizUserResponseRepo quizUserResponseRepo;

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getMyAttempts(IdentityContext identity, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("endedAt").descending());
        Long userId = requireUserId(identity);
        Page<QuizInstance> quizInstances = quizInstanceRepo.findByUserIdAndStatus(userId, QuizInstanceStatus.SUBMITTED, pageRequest);
        return convertToPageResDTO.convertPageResponse(quizInstances, pageRequest, this::toAttemptResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getQuizAttempts(Long quizId, Long groupId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("endedAt").descending());
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        if (!quiz.getLobby().getId().equals(groupId)) {
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz does not belong to group");
        }
        Page<QuizInstance> quizInstance = quizInstanceRepo.findByQuizIdAndStatus(quizId, QuizInstanceStatus.SUBMITTED,
                pageRequest);
        return convertToPageResDTO.convertPageResponse(quizInstance, pageRequest, this::toAttemptResDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptDetailResDTO getMyAttemptDetail(Long instanceId) {
        QuizInstance instance = quizInstanceRepo.findFullById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        return buildAttemptDetail(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptDetailResDTO getSubmissionDetail(
            Long groupId,
            Long quizId,
            Long instanceId
    ) {

        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        if (!instance.getQuiz().getId().equals(quizId)) {
            throw new ApiException(AppCode.BAD_REQUEST, "Quiz mismatch");
        }

        if (!instance.getQuiz().getLobby().getId().equals(groupId)) {
            throw new ApiException(AppCode.BAD_REQUEST, "Group mismatch");
        }

        return buildAttemptDetail(instance);
    }

    private Long requireUserId(IdentityContext identity) {
        if (identity == null || identity.getType() != IdentityType.USER || identity.getUserId() == null) {
            throw new ApiException(AppCode.UNAUTHORIZED, "User identity is required");
        }
        return identity.getUserId();
    }

    public AttemptResDTO toAttemptResDTO(QuizInstance instance) {

        int totalQuestions = instance.getTotalQuestions() != null ? instance.getTotalQuestions().intValue() : 0;
        int correctAnswers = instance.getCorrectAnswers() != null ? instance.getCorrectAnswers().intValue() : 0;
        double score = totalQuestions == 0
                ? 0
                : (correctAnswers * 100.0) / totalQuestions;

        long duration = 0;
        if (instance.getStartedAt() != null && instance.getEndedAt() != null) {
            duration = Math.max(0, Duration.between(instance.getStartedAt(), instance.getEndedAt()).toMillis());
        }

        return AttemptResDTO.builder()
                .id(instance.getId())
                .quizId(instance.getQuiz().getId())
                .userId(instance.getUser().getId())
                .title(instance.getQuiz().getTitle())
                .score(score)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .points(instance.getTotalPoints())
                .duration(duration)
                .completedAt(instance.getEndedAt())
                .badges(buildBadges(score)) // custom
                .badgeColors(buildBadgeColors(score)) // custom
                .build();
    }

    public AttemptDetailResDTO toAttemptDetailResDTO(QuizInstance instance) {

        int totalQuestions = instance.getTotalQuestions() != null ? instance.getTotalQuestions().intValue() : 0;
        int correctAnswers = instance.getCorrectAnswers() != null ? instance.getCorrectAnswers().intValue() : 0;
        double score = totalQuestions == 0
                ? 0
                : (correctAnswers * 100.0) / totalQuestions;

        long duration = 0;
        if (instance.getStartedAt() != null && instance.getEndedAt() != null) {
            duration = Math.max(0, Duration.between(instance.getStartedAt(), instance.getEndedAt()).toMillis());
        }

        return AttemptDetailResDTO.builder()
                .attemptInfo(AttemptResDTO.builder()
                        .id(instance.getId())
                        .quizId(instance.getQuiz().getId())
                        .userId(instance.getUser().getId())
                        .title(instance.getQuiz().getTitle())
                        .score(score)
                        .totalQuestions(totalQuestions)
                        .correctAnswers(correctAnswers)
                        .points(instance.getTotalPoints())
                        .duration(duration)
                        .completedAt(instance.getEndedAt())
                        .badges(buildBadges(score)) // custom
                        .badgeColors(buildBadgeColors(score)) // custom
                        .build())
                // answers sẽ được set ở service gọi đến repo để lấy chi tiết quiz result
                .answers(new ArrayList<>()) 
                .build();
    }

    private AttemptDetailResDTO buildAttemptDetail(QuizInstance instance) {
        AttemptDetailResDTO detail = toAttemptDetailResDTO(instance);
        QuizQuestionSnapshot snapshot = instance.getSnapshot();
        if (snapshot == null || snapshot.getQuestions() == null) {
            detail.setAnswers(new ArrayList<>());
            return detail;
        }

        Map<String, QuizUserResponse> responses = quizUserResponseRepo.findByQuizInstanceId(instance.getId()).stream()
                .collect(Collectors.toMap(QuizUserResponse::getQuestionSnapshotKey, r -> r, (a, b) -> a));

        List<UserAnswerResDTO> answers = new ArrayList<>();
        for (QuestionSnapshot qs : snapshot.getQuestions()) {
            QuizUserResponse response = responses.get(qs.getKey());

            List<AnswerResDTO> options = new ArrayList<>();
            for (int i = 0; i < qs.getAnswers().size(); i++) {
                AnswerSnapshot as = qs.getAnswers().get(i);
                options.add(AnswerResDTO.builder()
                        .id((long) i)
                        .answerText(as.getContent())
                        .answerType(as.getType())
                        .orderIndex(i + 1)
                        .build());
            }

            List<Long> selectedOptionIds = new ArrayList<>();
            if (response != null) {
                if (response.getSelectedAnswerIds() != null && !response.getSelectedAnswerIds().isEmpty()) {
                    selectedOptionIds.addAll(response.getSelectedAnswerIds());
                } else if (response.getSelectedAnswerId() != null) {
                    selectedOptionIds.add(response.getSelectedAnswerId());
                }
            }

            answers.add(UserAnswerResDTO.builder()
                    .id(response != null ? response.getId() : null)
                    .questionId(response != null ? response.getQuestionId() : null)
                    .questionName(qs.getContent())
                    .type(qs.getType())
                    .answerType(qs.getAnswerType())
                    .options(options)
                    .selectedOptionIds(selectedOptionIds)
                    .answerText(null)
                    .isCorrect(response != null ? response.isCorrect() : null)
                    .pointsEarned(response != null ? response.getPointsEarned() : 0L)
                    .build());
        }

        detail.setAnswers(answers);
        return detail;
    }

    private List<String> buildBadges(double score) {
        List<String> badges = new ArrayList<>();
        if (score >= 90) {
            badges.add("Gold");
        } else if (score >= 75) {
            badges.add("Silver");
        } else if (score >= 50) {
            badges.add("Bronze");
        }
        return badges;
    }

    private List<String> buildBadgeColors(double score) {
        List<String> colors = new ArrayList<>();
        if (score >= 90) {
            colors.add("Gold");
        } else if (score >= 75) {
            colors.add("Silver");
        } else if (score >= 50) {
            colors.add("Bronze");
        }
        return colors;
    }
}
