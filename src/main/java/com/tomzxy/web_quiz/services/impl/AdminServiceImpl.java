package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.admin.*;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.admin.*;
import com.tomzxy.web_quiz.enums.*;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.Lobby;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.Subject;
import com.tomzxy.web_quiz.models.NotificationUser.Notification;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.Permission;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepo userRepo;
    private final LobbyRepo lobbyRepo;
    private final QuizRepo quizRepo;
    private final QuizInstanceRepo quizInstanceRepo;
    private final RoleRepo roleRepo;
    private final SubjectRepo subjectRepo;
    private final NotificationRepo notificationRepo;
    private final QuestionBankRepo questionBankRepo;
    private final PermissionRepo permissionRepo;
    private final RolePermissionObjectRepo rolePermissionObjectRepo;

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ─── Dashboard ──────────────────────────────────────────────────────

    @Override
    public AdminDashboardResDTO getDashboard() {

        long totalUsers = userRepo.count();
        long totalGroups = lobbyRepo.count();
        long totalQuizzes = quizRepo.count();
        long totalResults = quizInstanceRepo.count();

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        long activeQuizzes = quizRepo.findAll().stream()
                .filter(q -> q.getStatus() == QuizStatus.OPENED)
                .count();

        long newUsers7d = userRepo.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(sevenDaysAgo))
                .count();
        long newGroups7d = lobbyRepo.findAll().stream()
                .filter(l -> l.getCreatedAt() != null && l.getCreatedAt().isAfter(sevenDaysAgo))
                .count();
        long newQuizzes7d = quizRepo.findAll().stream()
                .filter(q -> q.getCreatedAt() != null && q.getCreatedAt().isAfter(sevenDaysAgo))
                .count();
        long newResults7d = quizInstanceRepo.findAll().stream()
                .filter(qi -> qi.getCreatedAt() != null && qi.getCreatedAt().isAfter(sevenDaysAgo))
                .count();

        AdminDashboardResDTO.AdminDashboardCounts counts = AdminDashboardResDTO.AdminDashboardCounts.builder()
                .totalUsers(totalUsers)
                .totalGroups(totalGroups)
                .totalQuizzes(totalQuizzes)
                .totalResults(totalResults)
                .activeQuizzes(activeQuizzes)
                .newUsers7d(newUsers7d)
                .newGroups7d(newGroups7d)
                .newQuizzes7d(newQuizzes7d)
                .newResults7d(newResults7d)
                .build();

        // Trends: last 7 days
        List<AdminDashboardResDTO.AdminDashboardTrendPoint> trends = buildTrends(sevenDaysAgo);

        // Recent items: last 10
        List<AdminDashboardResDTO.AdminDashboardRecentItem> recent = buildRecentItems();

        return AdminDashboardResDTO.builder()
                .counts(counts)
                .trends(trends)
                .recent(recent)
                .build();
    }

    private List<AdminDashboardResDTO.AdminDashboardTrendPoint> buildTrends(LocalDateTime since) {
        List<AdminDashboardResDTO.AdminDashboardTrendPoint> trends = new ArrayList<>();
        LocalDate startDate = since.toLocalDate();
        LocalDate today = LocalDate.now();

        List<User> allUsers = userRepo.findAll();
        List<Lobby> allLobbies = lobbyRepo.findAll();
        List<Quiz> allQuizzes = quizRepo.findAll();
        List<QuizInstance> allInstances = quizInstanceRepo.findAll();

        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            LocalDate d = date;
            long usersCount = allUsers.stream()
                    .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().toLocalDate().equals(d))
                    .count();
            long groupsCount = allLobbies.stream()
                    .filter(l -> l.getCreatedAt() != null && l.getCreatedAt().toLocalDate().equals(d))
                    .count();
            long quizzesCount = allQuizzes.stream()
                    .filter(q -> q.getCreatedAt() != null && q.getCreatedAt().toLocalDate().equals(d))
                    .count();
            long resultsCount = allInstances.stream()
                    .filter(qi -> qi.getCreatedAt() != null && qi.getCreatedAt().toLocalDate().equals(d))
                    .count();

            trends.add(AdminDashboardResDTO.AdminDashboardTrendPoint.builder()
                    .date(d.toString())
                    .users(usersCount)
                    .groups(groupsCount)
                    .quizzes(quizzesCount)
                    .results(resultsCount)
                    .build());
        }
        return trends;
    }

    private List<AdminDashboardResDTO.AdminDashboardRecentItem> buildRecentItems() {
        List<AdminDashboardResDTO.AdminDashboardRecentItem> recent = new ArrayList<>();
        Pageable topN = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        userRepo.findAll(topN).forEach(u -> recent.add(
                AdminDashboardResDTO.AdminDashboardRecentItem.builder()
                        .id(u.getId())
                        .type("USER")
                        .title(u.getUserName())
                        .subtitle(u.getEmail())
                        .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().format(ISO_FMT) : null)
                        .status(mapUserStatus(u).name())
                        .build()));

        quizRepo.findAll(topN).forEach(q -> recent.add(
                AdminDashboardResDTO.AdminDashboardRecentItem.builder()
                        .id(q.getId())
                        .type("QUIZ")
                        .title(q.getTitle())
                        .subtitle(q.getHost() != null ? q.getHost().getUserName() : null)
                        .createdAt(q.getCreatedAt() != null ? q.getCreatedAt().format(ISO_FMT) : null)
                        .status(mapQuizStatus(q).name())
                        .build()));

        recent.sort((a, b) -> {
            if (a.getCreatedAt() == null)
                return 1;
            if (b.getCreatedAt() == null)
                return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        return recent.size() > 10 ? recent.subList(0, 10) : recent;
    }

    // ─── Users ──────────────────────────────────────────────────────────

    @Override
    public PageResDTO<AdminUserResDTO> getUsers(AdminListReqDTO req) {
        Pageable pageable = buildPageable(req);
        Page<User> page;

        if (req.getSearch() != null && !req.getSearch().isBlank()) {
            page = userRepo.searchUsers(req.getSearch(), true, pageable);
        } else {
            page = userRepo.findAll(pageable);
        }

        // Filter by status after query if specified
        List<AdminUserResDTO> items = page.getContent().stream()
                .filter(u -> {
                    if (req.getStatus() == null || req.getStatus().isBlank())
                        return true;
                    try {
                        AdminUserStatus filterStatus = AdminUserStatus.valueOf(req.getStatus().toUpperCase());
                        return mapUserStatus(u) == filterStatus;
                    } catch (IllegalArgumentException e) {
                        return true;
                    }
                })
                .map(this::toAdminUserRes)
                .toList();

        return PageResDTO.<AdminUserResDTO>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .total_page(page.getTotalPages())
                .total(page.getTotalElements())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public AdminUserResDTO updateUser(Long userId, AdminUserUpdateReqDTO req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (req.getStatus() != null) {
            switch (req.getStatus()) {
                case ACTIVE -> {
                    user.setActive(true);
                    user.setEmailVerified(true);
                }
                case PENDING -> {
                    user.setActive(true);
                    user.setEmailVerified(false);
                }
                case BANNED -> user.setActive(false);
            }
        }

        if (req.getRoles() != null) {
            Set<Role> newRoles = req.getRoles().stream()
                    .map(roleName -> roleRepo.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
        }

        userRepo.save(user);
        return toAdminUserRes(user);
    }

    // ─── Groups ─────────────────────────────────────────────────────────

    @Override
    public PageResDTO<AdminGroupResDTO> getGroups(AdminListReqDTO req) {
        Pageable pageable = buildPageable(req);
        Page<Lobby> page;

        if (req.getSearch() != null && !req.getSearch().isBlank()) {
            page = lobbyRepo.searchByLobbyName(req.getSearch(), pageable);
        } else {
            page = lobbyRepo.findAll(pageable);
        }

        List<AdminGroupResDTO> items = page.getContent().stream()
                .filter(l -> {
                    if (req.getStatus() == null || req.getStatus().isBlank())
                        return true;
                    try {
                        AdminGroupStatus filterStatus = AdminGroupStatus.valueOf(req.getStatus().toUpperCase());
                        return mapGroupStatus(l) == filterStatus;
                    } catch (IllegalArgumentException e) {
                        return true;
                    }
                })
                .map(this::toAdminGroupRes)
                .toList();

        return PageResDTO.<AdminGroupResDTO>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .total_page(page.getTotalPages())
                .total(page.getTotalElements())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public AdminGroupResDTO updateGroup(Long groupId, AdminGroupUpdateReqDTO req) {
        Lobby lobby = lobbyRepo.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));

        if (req.getStatus() != null) {
            switch (req.getStatus()) {
                case ACTIVE -> lobby.setActive(true);
                case ARCHIVED, BLOCKED -> lobby.setActive(false);
            }
        }

        lobbyRepo.save(lobby);
        return toAdminGroupRes(lobby);
    }

    // ─── Quizzes ────────────────────────────────────────────────────────

    @Override
    public PageResDTO<AdminQuizResDTO> getQuizzes(AdminListReqDTO req) {
        Pageable pageable = buildPageable(req);
        Page<Quiz> page;

        if (req.getSearch() != null && !req.getSearch().isBlank()) {
            page = quizRepo.searchByTitleOrDescription(req.getSearch(), pageable);
        } else if (req.getGroupId() != null) {
            page = quizRepo.findByLobbyId(req.getGroupId(), pageable);
        } else {
            page = quizRepo.findAll(pageable);
        }

        List<AdminQuizResDTO> items = page.getContent().stream()
                .filter(q -> {
                    if (req.getStatus() != null && !req.getStatus().isBlank()) {
                        try {
                            AdminQuizStatus filterStatus = AdminQuizStatus.valueOf(req.getStatus().toUpperCase());
                            if (mapQuizStatus(q) != filterStatus)
                                return false;
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    if (req.getVisibility() != null && !req.getVisibility().isBlank()) {
                        try {
                            AdminQuizVisibility filterVis = AdminQuizVisibility
                                    .valueOf(req.getVisibility().toUpperCase());
                            if (mapQuizVisibility(q) != filterVis)
                                return false;
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    return true;
                })
                .map(this::toAdminQuizRes)
                .toList();

        return PageResDTO.<AdminQuizResDTO>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .total_page(page.getTotalPages())
                .total(page.getTotalElements())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public AdminQuizResDTO updateQuiz(Long quizId, AdminQuizUpdateReqDTO req) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        if (req.getStatus() != null) {
            switch (req.getStatus()) {
                case DRAFT -> quiz.setStatus(QuizStatus.DRAFT);
                case PUBLISHED -> quiz.setStatus(QuizStatus.OPENED);
                case ARCHIVED -> quiz.setStatus(QuizStatus.ARCHIVED);
            }
        }

        if (req.getVisibility() != null) {
            switch (req.getVisibility()) {
                case PUBLIC -> quiz.setVisibility(QuizVisibility.PUBLIC);
                case PRIVATE -> quiz.setVisibility(QuizVisibility.PRIVATE);
                case GROUP -> quiz.setVisibility(QuizVisibility.CLASS_ONLY);
            }
        }

        quizRepo.save(quiz);
        return toAdminQuizRes(quiz);
    }

    // ─── Results ────────────────────────────────────────────────────────

    @Override
    public PageResDTO<AdminResultResDTO> getResults(AdminListReqDTO req) {
        Pageable pageable = buildPageable(req);
        Page<QuizInstance> page;

        if (req.getQuizId() != null) {
            page = quizInstanceRepo.findByQuizIdAndStatus(req.getQuizId(), null, pageable);
            // fallback: get all for page if specific status filter not possible
            if (page == null || page.isEmpty()) {
                List<QuizInstance> all = quizInstanceRepo.findByQuizId(req.getQuizId());
                page = new PageImpl<>(all, pageable, all.size());
            }
        } else {
            page = quizInstanceRepo.findAll(pageable);
        }

        List<AdminResultResDTO> items = page.getContent().stream()
                .filter(qi -> {
                    if (req.getStatus() != null && !req.getStatus().isBlank()) {
                        try {
                            AdminResultStatus filterStatus = AdminResultStatus.valueOf(req.getStatus().toUpperCase());
                            if (mapResultStatus(qi) != filterStatus)
                                return false;
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    if (req.getUserId() != null
                            && (qi.getUser() == null || !qi.getUser().getId().equals(req.getUserId()))) {
                        return false;
                    }
                    if (req.getGroupId() != null && (qi.getQuiz().getLobby() == null
                            || !qi.getQuiz().getLobby().getId().equals(req.getGroupId()))) {
                        return false;
                    }
                    return true;
                })
                .map(this::toAdminResultRes)
                .toList();

        return PageResDTO.<AdminResultResDTO>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .total_page(page.getTotalPages())
                .total(page.getTotalElements())
                .items(items)
                .build();
    }

    // ─── Mapping Helpers ────────────────────────────────────────────────

    private AdminUserStatus mapUserStatus(User user) {
        if (!user.isActive())
            return AdminUserStatus.BANNED;
        if (!user.isEmailVerified())
            return AdminUserStatus.PENDING;
        return AdminUserStatus.ACTIVE;
    }

    private AdminGroupStatus mapGroupStatus(Lobby lobby) {
        return lobby.isActive() ? AdminGroupStatus.ACTIVE : AdminGroupStatus.BLOCKED;
    }

    private AdminQuizStatus mapQuizStatus(Quiz quiz) {
        if (quiz.getStatus() == null)
            return AdminQuizStatus.DRAFT;
        return switch (quiz.getStatus()) {
            case DRAFT -> AdminQuizStatus.DRAFT;
            case OPENED, PAUSED, CLOSED -> AdminQuizStatus.PUBLISHED;
            case ARCHIVED -> AdminQuizStatus.ARCHIVED;
        };
    }

    private AdminQuizVisibility mapQuizVisibility(Quiz quiz) {
        if (quiz.getVisibility() == null)
            return AdminQuizVisibility.PRIVATE;
        return switch (quiz.getVisibility()) {
            case PUBLIC -> AdminQuizVisibility.PUBLIC;
            case PRIVATE -> AdminQuizVisibility.PRIVATE;
            case CLASS_ONLY -> AdminQuizVisibility.GROUP;
        };
    }

    private AdminResultStatus mapResultStatus(QuizInstance instance) {
        if (instance.getStatus() == null)
            return AdminResultStatus.IN_PROGRESS;
        return switch (instance.getStatus()) {
            case IN_PROGRESS -> AdminResultStatus.IN_PROGRESS;
            case SUBMITTED -> AdminResultStatus.DONE;
            case TIMED_OUT, ABANDONED -> AdminResultStatus.MISSED;
        };
    }

    // ─── DTO Converters ─────────────────────────────────────────────────

    private AdminUserResDTO toAdminUserRes(User user) {
        return AdminUserResDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream().map(Role::getName).toList()
                        : List.of())
                .status(mapUserStatus(user))
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(ISO_FMT) : null)
                .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().format(ISO_FMT) : null)
                .quizTakenCount(user.getQuizInstances() != null ? user.getQuizInstances().size() : 0)
                .groupCount(user.getLobbies() != null ? user.getLobbies().size() : 0)
                .build();
    }

    private AdminGroupResDTO toAdminGroupRes(Lobby lobby) {
        return AdminGroupResDTO.builder()
                .id(lobby.getId())
                .name(lobby.getLobbyName())
                .ownerName(lobby.getHost() != null ? lobby.getHost().getUserName() : null)
                .memberCount(lobby.getMembers() != null ? lobby.getMembers().size() : 0)
                .quizCount(lobby.getQuizzes() != null ? lobby.getQuizzes().size() : 0)
                .status(mapGroupStatus(lobby))
                .createdAt(lobby.getCreatedAt() != null ? lobby.getCreatedAt().format(ISO_FMT) : null)
                .build();
    }

    private AdminQuizResDTO toAdminQuizRes(Quiz quiz) {
        return AdminQuizResDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .subject(quiz.getSubject() != null ? quiz.getSubject().getSubjectName() : null)
                .ownerName(quiz.getHost() != null ? quiz.getHost().getUserName() : null)
                .groupName(quiz.getLobby() != null ? quiz.getLobby().getLobbyName() : null)
                .status(mapQuizStatus(quiz))
                .visibility(mapQuizVisibility(quiz))
                .questionCount(quiz.getTotalQuestion() != null ? quiz.getTotalQuestion().intValue() : 0)
                .attemptsCount(quiz.getTotalAttempts() != null ? quiz.getTotalAttempts().intValue() : 0)
                .createdAt(quiz.getCreatedAt() != null ? quiz.getCreatedAt().format(ISO_FMT) : null)
                .build();
    }

    private AdminResultResDTO toAdminResultRes(QuizInstance instance) {
        return AdminResultResDTO.builder()
                .id(instance.getId())
                .quizTitle(instance.getQuiz() != null ? instance.getQuiz().getTitle() : null)
                .userName(instance.getUser() != null ? instance.getUser().getUserName() : null)
                .groupName(instance.getQuiz() != null && instance.getQuiz().getLobby() != null
                        ? instance.getQuiz().getLobby().getLobbyName()
                        : null)
                .status(mapResultStatus(instance))
                .score(instance.getScorePercentage())
                .submittedAt(instance.getEndedAt() != null ? instance.getEndedAt().format(ISO_FMT) : null)
                .durationSeconds(instance.getElapsedTimeSeconds() != null
                        ? instance.getElapsedTimeSeconds().intValue()
                        : null)
                .build();
    }

    @Override
    public AdminUserDetailResDTO getUserDetail(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        List<AdminGroupResDTO> groups = user.getLobbies() == null ? List.of()
                : user.getLobbies().stream().map(lm -> toAdminGroupRes(lm.getLobby())).toList();

        List<AdminResultResDTO> results = user.getQuizInstances() == null ? List.of()
                : user.getQuizInstances().stream()
                        .sorted((a, b) -> b.getCreatedAt() != null && a.getCreatedAt() != null
                                ? b.getCreatedAt().compareTo(a.getCreatedAt())
                                : 0)
                        .limit(10)
                        .map(this::toAdminResultRes).toList();

        return AdminUserDetailResDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePictureUrl())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .status(mapUserStatus(user))
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(ISO_FMT) : null)
                .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().format(ISO_FMT) : null)
                .quizTakenCount(user.getQuizInstances() != null ? user.getQuizInstances().size() : 0)
                .groupCount(user.getLobbies() != null ? user.getLobbies().size() : 0)
                .groups(groups)
                .recentResults(results)
                .build();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        user.setActive(false);
        userRepo.save(user);
    }

    @Override
    public AdminGroupDetailResDTO getGroupDetail(Long groupId) {
        Lobby lobby = lobbyRepo.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));

        List<AdminMemberResDTO> members = lobby.getMembers() == null ? List.of()
                : lobby.getMembers().stream().map(m -> AdminMemberResDTO.builder()
                        .userId(m.getUser().getId())
                        .userName(m.getUser().getUserName())
                        .email(m.getUser().getEmail())
                        .role(m.getRole().name())
                        .joinedAt(m.getJoinedAt() != null ? m.getJoinedAt().format(ISO_FMT) : null)
                        .build()).toList();

        List<AdminQuizResDTO> quizzes = lobby.getQuizzes() == null ? List.of()
                : lobby.getQuizzes().stream().map(this::toAdminQuizRes).toList();

        return AdminGroupDetailResDTO.builder()
                .id(lobby.getId())
                .name(lobby.getLobbyName())
                .ownerName(lobby.getHost() != null ? lobby.getHost().getUserName() : null)
                .codeInvite(lobby.getCodeInvite())
                .memberCount(lobby.getMembers() != null ? lobby.getMembers().size() : 0)
                .quizCount(lobby.getQuizzes() != null ? lobby.getQuizzes().size() : 0)
                .announcementCount(lobby.getNotifications() != null ? lobby.getNotifications().size() : 0)
                .status(mapGroupStatus(lobby))
                .createdAt(lobby.getCreatedAt() != null ? lobby.getCreatedAt().format(ISO_FMT) : null)
                .members(members)
                .quizzes(quizzes)
                .build();
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId) {
        Lobby lobby = lobbyRepo.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));
        lobby.setActive(false);
        lobbyRepo.save(lobby);
    }

    @Override
    public AdminQuizDetailResDTO getQuizDetail(Long quizId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        return AdminQuizDetailResDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .subject(quiz.getSubject() != null ? quiz.getSubject().getSubjectName() : null)
                .ownerName(quiz.getHost() != null ? quiz.getHost().getUserName() : null)
                .groupName(quiz.getLobby() != null ? quiz.getLobby().getLobbyName() : null)
                .status(mapQuizStatus(quiz))
                .visibility(mapQuizVisibility(quiz))
                .questionCount(quiz.getTotalQuestion() != null ? quiz.getTotalQuestion().intValue() : 0)
                .attemptsCount(quiz.getTotalAttempts() != null ? quiz.getTotalAttempts().intValue() : 0)
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .maxAttempt(quiz.getMaxAttempt())
                .startDate(quiz.getStartDate() != null ? quiz.getStartDate().format(ISO_FMT) : null)
                .endDate(quiz.getEndDate() != null ? quiz.getEndDate().format(ISO_FMT) : null)
                .createdAt(quiz.getCreatedAt() != null ? quiz.getCreatedAt().format(ISO_FMT) : null)
                .build();
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));
        quiz.setStatus(QuizStatus.ARCHIVED);
        quizRepo.save(quiz);
    }

    @Override
    public AdminResultDetailResDTO getResultDetail(Long resultId) {
        QuizInstance qi = quizInstanceRepo.findById(resultId)
                .orElseThrow(() -> new NotFoundException("Result not found: " + resultId));

        int correctCount = 0;
        int wrongCount = 0;
        int skippedCount = 0;
        int totalQuestions = qi.getQuiz() != null ? qi.getQuiz().getTotalQuestion() : 0;

        if (qi.getUserResponses() != null) {
            for (com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse r : qi.getUserResponses()) {
                if (r.isSkipped())
                    skippedCount++;
                else if (r.isCorrect())
                    correctCount++;
                else
                    wrongCount++;
            }
        }

        return AdminResultDetailResDTO.builder()
                .id(qi.getId())
                .quizTitle(qi.getQuiz() != null ? qi.getQuiz().getTitle() : null)
                .userName(qi.getUser() != null ? qi.getUser().getUserName() : null)
                .groupName(
                        qi.getQuiz() != null && qi.getQuiz().getLobby() != null ? qi.getQuiz().getLobby().getLobbyName()
                                : null)
                .status(mapResultStatus(qi))
                .score(qi.getScorePercentage())
                .totalPoints(qi.getTotalPoints() != null ? qi.getTotalPoints().intValue() : 0)
                .earnedPoints(qi.getEarnedPoints() != null ? qi.getEarnedPoints().intValue() : 0)
                .startedAt(qi.getStartedAt() != null ? qi.getStartedAt().format(ISO_FMT) : null)
                .submittedAt(qi.getEndedAt() != null ? qi.getEndedAt().format(ISO_FMT) : null)
                .durationSeconds(qi.getElapsedTimeSeconds() != null ? qi.getElapsedTimeSeconds().intValue() : null)
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .skippedCount(skippedCount)
                .build();
    }

    @Override
    @Transactional
    public void deleteResult(Long resultId) {
        quizInstanceRepo.deleteById(resultId);
    }

    @Override
    public List<AdminSubjectResDTO> getSubjects() {
        return subjectRepo.findAll().stream().map(s -> AdminSubjectResDTO.builder()
                .id(s.getId())
                .subjectName(s.getSubjectName())
                .description(s.getDescription())
                .quizCount(s.getQuizzes() != null ? s.getQuizzes().size() : 0)
                .createdAt(s.getCreatedAt() != null ? s.getCreatedAt().format(ISO_FMT) : null)
                .build()).toList();
    }

    @Override
    @Transactional
    public void createSubject(AdminSubjectReqDTO req) {
        Subject subject = Subject.builder()
                .subjectName(req.getSubjectName())
                .description(req.getDescription())
                .build();
        subjectRepo.save(subject);
    }

    @Override
    @Transactional
    public AdminSubjectResDTO updateSubject(Long subjectId, AdminSubjectReqDTO req) {
        Subject subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Subject not found: " + subjectId));
        subject.setSubjectName(req.getSubjectName());
        subject.setDescription(req.getDescription());
        subjectRepo.save(subject);

        return AdminSubjectResDTO.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .description(subject.getDescription())
                .quizCount(subject.getQuizzes() != null ? subject.getQuizzes().size() : 0)
                .createdAt(subject.getCreatedAt() != null ? subject.getCreatedAt().format(ISO_FMT) : null)
                .build();
    }

    @Override
    @Transactional
    public void deleteSubject(Long subjectId) {
        Subject subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Subject not found: " + subjectId));
        subject.setActive(false);
        subjectRepo.save(subject);
    }

    @Override
    public List<AdminRoleResDTO> getRoles() {
        return roleRepo.findAll().stream().map(r -> AdminRoleResDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .userCount(r.getUser() != null ? r.getUser().size() : 0)
                .permissions(r.getRolePermissionObjects() != null
                        ? r.getRolePermissionObjects().stream()
                                .map(p -> p.getPermission().getPermissionName() + "_"
                                        + p.getRolePermissionId().getObjectType())
                                .toList()
                        : new ArrayList<String>())
                .build()).toList();
    }

    @Override
    public AdminRoleResDTO getRole(Long roleId) {
        Role r = roleRepo.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleId));
        return AdminRoleResDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .userCount(r.getUser() != null ? r.getUser().size() : 0)
                .permissions(r.getRolePermissionObjects() != null
                        ? r.getRolePermissionObjects().stream()
                                .map(p -> p.getPermission().getPermissionName() + "_"
                                        + p.getRolePermissionId().getObjectType())
                                .toList()
                        : new ArrayList<String>())
                .build();
    }

    @Override
    public AdminRoleResDTO createRole(AdminRoleReqDTO req) {
        if (roleRepo.findByName(req.getName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists: " + req.getName());
        }
        Role role = roleRepo.save(new Role(req.getName()));
        assignPermissionsToRole(role, req.getPermissions());
        return getRole(role.getId());
    }

    @Override
    public AdminRoleResDTO updateRole(Long roleId, AdminRoleReqDTO req) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleId));

        if (!role.getName().equals(req.getName()) && roleRepo.findByName(req.getName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists: " + req.getName());
        }

        role.setName(req.getName());
        roleRepo.save(role);

        rolePermissionObjectRepo.deleteAll(role.getRolePermissionObjects());
        role.getRolePermissionObjects().clear();
        roleRepo.save(role);

        assignPermissionsToRole(role, req.getPermissions());
        return getRole(roleId);
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleId));

        if (role.getUser() != null && !role.getUser().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete role that is still assigned to users");
        }

        rolePermissionObjectRepo.deleteAll(role.getRolePermissionObjects());
        roleRepo.delete(role);
    }

    private void assignPermissionsToRole(Role role, List<String> permissionsList) {
        if (permissionsList == null || permissionsList.isEmpty())
            return;

        for (String permStr : permissionsList) {
            String[] parts = permStr.split("_", 2);
            if (parts.length != 2)
                continue;

            String action = parts[0];
            String objectType = parts[1];

            Permission permission = permissionRepo.findById(action)
                    .orElseGet(() -> permissionRepo.save(new Permission(action, action)));

            com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject rpo = new com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject(
                    role, permission, objectType);

            rolePermissionObjectRepo.save(rpo);
        }
    }

    @Override
    public PageResDTO<AdminNotificationResDTO> getNotifications(int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> p = notificationRepo.findAll(pr);

        List<AdminNotificationResDTO> items = p.getContent().stream().map(n -> AdminNotificationResDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .groupName(n.getLobby() != null ? n.getLobby().getLobbyName() : null)
                .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().format(ISO_FMT) : null)
                .build()).toList();

        return PageResDTO.<AdminNotificationResDTO>builder()
                .page(p.getNumber())
                .size(p.getSize())
                .total_page(p.getTotalPages())
                .total(p.getTotalElements())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepo.deleteById(notificationId);
    }

    @Override
    public PageResDTO<AdminQuestionBankResDTO> getQuestionBanks(int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<QuestionBank> p = questionBankRepo.findAll(pr);

        List<AdminQuestionBankResDTO> items = p.getContent().stream().map(qb -> AdminQuestionBankResDTO.builder()
                .id(qb.getId())
                .ownerName(qb.getOwner() != null ? qb.getOwner().getUserName() : null)
                .folderCount(qb.getFolders() != null ? qb.getFolders().size() : 0)
                .questionCount(qb.getQuestions() != null ? qb.getQuestions().size() : 0)
                .createdAt(qb.getCreatedAt() != null ? qb.getCreatedAt().format(ISO_FMT) : null)
                .build()).toList();

        return PageResDTO.<AdminQuestionBankResDTO>builder()
                .page(p.getNumber())
                .size(p.getSize())
                .total_page(p.getTotalPages())
                .total(p.getTotalElements())
                .items(items)
                .build();
    }

    // ─── Pagination Helper ──────────────────────────────────────────────

    private Pageable buildPageable(AdminListReqDTO req) {
        int page = req.getPage() != null ? req.getPage() : 0;
        int size = req.getSize() != null ? req.getSize() : 10;

        if (req.getSortBy() != null && !req.getSortBy().isBlank()) {
            Sort.Direction dir = "DESC".equalsIgnoreCase(req.getDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(dir, req.getSortBy()));
        }

        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
