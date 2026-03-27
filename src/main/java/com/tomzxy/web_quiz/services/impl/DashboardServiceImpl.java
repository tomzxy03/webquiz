package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.responses.dashboard.*;
import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import com.tomzxy.web_quiz.enums.LobbyRole;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.enums.QuizStatus;
import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

        private final UserRepo userRepo;
        private final QuizInstanceRepo quizInstanceRepo;
        private final QuizRepo quizRepo;
        private final LobbyMemberRepo lobbyMemberRepo;
        private final QuizUserResponseRepo quizUserResponseRepo;

        private static final List<QuizInstanceStatus> COMPLETED_STATUSES = List.of(
                        QuizInstanceStatus.SUBMITTED, QuizInstanceStatus.TIMED_OUT);
        private static final int MAX_IN_PROGRESS = 10;
        private static final int MAX_RECENT = 10;
        private static final int MAX_DRAFTS = 10;
        private static final int MAX_UPCOMING = 5;

        @Override
        public DashboardSummaryResDTO getSummary(Long userId) {
                log.info("Building dashboard summary for user {}", userId);

                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // 1. User info
                UserResDTO userDto = UserResDTO.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .profilePictureUrl(user.getProfilePictureUrl())
                                .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                                .build();

                // 2. User stats (3 independent count queries — very fast with indexes)
                long inProgressCount = quizInstanceRepo.countByUserIdAndStatus(userId, QuizInstanceStatus.IN_PROGRESS);
                long completedCount = quizInstanceRepo.countByUserIdAndStatusIn(userId, COMPLETED_STATUSES);
                long totalTaken = inProgressCount + completedCount;

                DashboardUserStats stats = DashboardUserStats.builder()
                                .totalQuizzesTaken((int) totalTaken)
                                .inProgressCount((int) inProgressCount)
                                .completedCount((int) completedCount)
                                .build();

                List<QuizInstance> inProgressInstances = quizInstanceRepo
                                .findInProgressWithQuizByUserId(userId, QuizInstanceStatus.IN_PROGRESS);

                Map<Long, Long> answeredCountByInstanceId = new HashMap<>();
                if (!inProgressInstances.isEmpty()) {
                        List<Long> inProgressIds = inProgressInstances.stream()
                                        .map(QuizInstance::getId)
                                        .collect(Collectors.toList());
                        answeredCountByInstanceId.putAll(
                                        quizUserResponseRepo
                                                        .countByQuizInstanceId(inProgressIds)
                                                        .stream()
                                                        .collect(Collectors.toMap(
                                                                        r -> (Long) r[0],
                                                                        r -> (Long) r[1])));
                }

                List<InProgressQuizInstance> inProgressDtos = inProgressInstances.stream()
                                .limit(MAX_IN_PROGRESS)
                                .map(qi -> {
                                        Quiz quiz = qi.getQuiz();
                                        long answeredCount = answeredCountByInstanceId.getOrDefault(qi.getId(), 0L);
                                        int totalQuestions = quiz.getTotalQuestion() != null
                                                        ? quiz.getTotalQuestion().intValue()
                                                        : 0;

                                        // Calculate remaining time
                                        Long timeRemaining = null;
                                        if (quiz.getTimeLimitMinutes() != null) {
                                                long elapsedSeconds = Duration
                                                                .between(qi.getStartedAt(), LocalDateTime.now())
                                                                .getSeconds();
                                                long limitSeconds = quiz.getTimeLimitMinutes() * 60L;
                                                timeRemaining = Math.max(0, limitSeconds - elapsedSeconds);
                                        }

                                        return InProgressQuizInstance.builder()
                                                        .id(qi.getId())
                                                        .quizId(quiz.getId())
                                                        .quizTitle(quiz.getTitle())
                                                        .answeredCount((int) answeredCount)
                                                        .totalQuestions(totalQuestions)
                                                        .timeRemainingSeconds(timeRemaining)
                                                        .resumedAt(qi.getUpdatedAt() != null
                                                                        ? qi.getUpdatedAt().toString()
                                                                        : qi.getStartedAt().toString())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // 4. Recent & upcoming quizzes
                Map<String, QuizRecentItem> map = new LinkedHashMap<>();

                // 4a. Recent completed instances
                Pageable recentPageable = PageRequest.of(0, MAX_RECENT);
                List<QuizInstance> recentInstances = quizInstanceRepo
                                .findRecentByUserIdAndStatusIn(userId, COMPLETED_STATUSES, recentPageable);

                for (QuizInstance qi : recentInstances) {
                        Quiz quiz = qi.getQuiz();
                        String status = mapToDashboardStatus(quiz, qi);

                        map.put("instance:" + qi.getId(), QuizRecentItem.builder()
                                        .id(qi.getId())
                                        .quizId(quiz.getId())
                                        .quizTitle(quiz.getTitle())
                                        .status(status)
                                        .submittedAt(qi.getEndedAt() != null ? qi.getEndedAt().toString() : null)
                                        .startDate(quiz.getStartDate() != null ? quiz.getStartDate().toString() : null)
                                        .endDate(quiz.getEndDate() != null ? quiz.getEndDate().toString() : null)
                                        .build());
                }

                // 4b. In-progress items (also included in recent)
                for (QuizInstance qi : inProgressInstances) {
                        if (map.size() >= MAX_RECENT)
                                break;
                        Quiz quiz = qi.getQuiz();
                        String status = mapToDashboardStatus(quiz, qi);

                        map.put("instance:" + qi.getId(), QuizRecentItem.builder()
                                        .id(qi.getId())
                                        .quizId(quiz.getId())
                                        .quizTitle(quiz.getTitle())
                                        .status(status)
                                        .startDate(quiz.getStartDate() != null ? quiz.getStartDate().toString() : null)
                                        .endDate(quiz.getEndDate() != null ? quiz.getEndDate().toString() : null)
                                        .build());
                }

                // 4c. Upcoming quizzes from user's groups
                List<LobbyMember> memberships = lobbyMemberRepo.findMembershipsWithLobbyByUserId(userId);
                List<Long> lobbyIds = memberships.stream()
                                .map(lm -> lm.getLobby().getId())
                                .collect(Collectors.toList());

                Map<Long, Long> openQuizCountByLobbyId = new HashMap<>();
                if (!lobbyIds.isEmpty()) {
                        openQuizCountByLobbyId.putAll(
                                        quizRepo.countByLobbyIdInAndStatus(lobbyIds, QuizStatus.OPENED)
                                        .stream()
                                        .collect(Collectors.toMap(r -> (Long) r[0],r -> (Long) r[1])));
                }
                if (!lobbyIds.isEmpty()) {
                        Pageable upcomingPageable = PageRequest.of(0, MAX_UPCOMING);
                        List<Quiz> upcomingQuizzes = quizRepo.findUpcomingByLobbyIds(
                                        lobbyIds, QuizStatus.OPENED, LocalDateTime.now(), upcomingPageable);
                        Set<Long> existingQuizIds = map.values().stream()
                                        .map(QuizRecentItem::getQuizId)
                                        .collect(Collectors.toSet());

                        for (Quiz quiz : upcomingQuizzes) {
                                if (map.size() >= MAX_RECENT)
                                        break;

                                if (existingQuizIds.contains(quiz.getId())) {
                                        continue;
                                }

                                // Check if user has started it
                                boolean userStarted = quizInstanceRepo.existsByQuizIdAndUserId(quiz.getId(), userId);
                                if (userStarted) {
                                        continue;
                                }

                                String status = mapToDashboardStatus(quiz, null);

                                if (status != null) {
                                        map.put("quiz:" + quiz.getId(), QuizRecentItem.builder()
                                                        .id(quiz.getId())
                                                        .quizId(quiz.getId())
                                                        .quizTitle(quiz.getTitle())
                                                        .status(status)
                                                        .startDate(quiz.getStartDate() != null
                                                                        ? quiz.getStartDate().toString()
                                                                        : null)
                                                        .endDate(quiz.getEndDate() != null
                                                                        ? quiz.getEndDate().toString()
                                                                        : null)
                                                        .build());
                                }
                        }
                }

                // 5. Groups summary
                List<DashboardGroupSummary> groupSummaries = memberships.stream()
                                .map(lm -> {
                                        Lobby lobby = lm.getLobby();
                                        // Determine role string
                                        String roleStr;
                                        if (lobby.getHost() != null && lobby.getHost().getId().equals(userId)) {
                                                roleStr = "OWNER";
                                        } else if (lm.getRole() == LobbyRole.HOST) {
                                                roleStr = "HOST";
                                        } else {
                                                roleStr = "MEMBER";
                                        }

                                        long openCount = openQuizCountByLobbyId.getOrDefault(lobby.getId(), 0L);

                                        return DashboardGroupSummary.builder()
                                                        .id(lobby.getId())
                                                        .name(lobby.getLobbyName())
                                                        .role(roleStr)
                                                        .openQuizzesCount((int) openCount)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // 6. Draft quizzes
                Pageable draftPageable = PageRequest.of(0, MAX_DRAFTS);
                List<Quiz> drafts = quizRepo.findDraftsByHostId(userId, QuizStatus.DRAFT, draftPageable);

                List<DraftQuizItem> draftItems = drafts.stream()
                                .map(q -> DraftQuizItem.builder()
                                                .id(q.getId())
                                                .title(q.getTitle())
                                                .subject(q.getSubject() != null ? q.getSubject().getSubjectName()
                                                                : null)
                                                .questionCount(q.getTotalQuestion() != null
                                                                ? q.getTotalQuestion().intValue()
                                                                : 0)
                                                .updatedAt(q.getUpdatedAt() != null ? q.getUpdatedAt().toString()
                                                                : null)
                                                .build())
                                .collect(Collectors.toList());

                // Build final response
                return DashboardSummaryResDTO.builder()
                                .user(userDto)
                                .userStats(stats)
                                .inProgressInstances(inProgressDtos)
                                .recentAndUpcoming(new ArrayList<>(map.values()))
                                .groups(groupSummaries)
                                .draftQuizzes(draftItems)
                                .build();
        }

        private String mapToDashboardStatus(Quiz quiz, QuizInstance instance) {
                LocalDateTime now = LocalDateTime.now();

                if (instance != null) {
                        switch (instance.getStatus()) {
                                case IN_PROGRESS:
                                        return "IN_PROGRESS";
                                case SUBMITTED:
                                case TIMED_OUT:
                                        return "DONE";
                                case ABANDONED:
                                        return "MISSED";
                        }
                }

                if (quiz.getStatus() == QuizStatus.DRAFT || quiz.getStatus() == QuizStatus.ARCHIVED) {
                        return null;
                }

                if (quiz.getStartDate() != null && quiz.getStartDate().isAfter(now)) {
                        return "UPCOMING";
                }

                if (quiz.getEndDate() != null && quiz.getEndDate().isBefore(now)) {
                        return "MISSED";
                }

                if (quiz.getStatus() == QuizStatus.OPENED) {
                        return "AVAILABLE";
                }

                if (quiz.getStatus() == QuizStatus.PAUSED) {
                        return "PAUSED";
                }

                return "UNKNOWN";
        }
}
