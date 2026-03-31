package com.tomzxy.web_quiz.configs.init;

import com.github.javafaker.Faker;
import com.tomzxy.web_quiz.containts.PredefinedRole;
import com.tomzxy.web_quiz.enums.*;
import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.models.Host.LobbyMemberId;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.NotificationUser.Notification;
import com.tomzxy.web_quiz.models.NotificationUser.NotificationUserId;
import com.tomzxy.web_quiz.models.NotificationUser.UserNotification;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizConfig;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionId;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.Quiz.QuestionLayout;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.QuizUser.QuizUserResponse;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.snapshot.AnswerSnapshot;
import com.tomzxy.web_quiz.models.snapshot.QuestionSnapshot;
import com.tomzxy.web_quiz.models.snapshot.QuizQuestionSnapshot;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class DataSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final SubjectRepo subjectRepo;
    private final QuestionBankRepo questionBankRepo;
    private final FolderRepo folderRepo;
    private final QuestionRepo questionRepo;
    private final AnswerRepo answerRepo;
    private final QuizRepo quizRepo;
    private final LobbyRepo lobbyRepo;
    private final QuizQuestionLinkRepo quizQuestionLinkRepo;
    private final QuizInstanceRepo quizInstanceRepo;
    private final QuizUserResponseRepo quizUserResponseRepo;
    private final NotificationRepo notificationRepo;
    private final NotificationUserRepo notificationUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final QuestionService questionService;

    private final Faker faker = new Faker(new Locale("vi"));
    private static final String DEFAULT_PASSWORD = "123456";

    private static final int SUBJECT_COUNT = 10;
    private static final int USER_COUNT = 10;
    private static final int QUIZ_COUNT = 40;
    private static final int LOBBY_COUNT = 20;
    private static final int FOLDERS_PER_USER = 3;
    private static final int QUESTIONS_PER_FOLDER = 6;
    private static final int QUESTIONS_PER_BANK = 6;
    private static final int NOTIFICATION_COUNT = 6;
    private static final int QUIZ_INSTANCE_SEED_USERS = 30;
    private static final int QUIZ_INSTANCE_SEED_QUIZZES = 12;
    private static final double PUBLIC_QUIZ_RATIO = 0.5;
    private static final String LOAD_TEST_EMAIL_DOMAIN = "example.com";
    private static final String LOAD_TEST_QUIZ_TITLE = "Load Test Quiz";
    private static final String LOAD_TEST_LOBBY_NAME = "Load Test Group";
    private static final String LOAD_TEST_GROUP_QUIZ_TITLE = "Load Test Group Quiz";

    private static final List<String> VN_SUBJECTS = Arrays.asList(
            "Toan hoc", "Vat ly", "Hoa hoc", "Sinh hoc", "Lich su",
            "Dia ly", "Ngu van", "Tieng Anh", "Tin hoc", "Giao duc cong dan"
    );
    private static final List<String> VN_DEPARTMENTS = Arrays.asList(
            "Khoa hoc tu nhien", "Khoa hoc xa hoi", "Ngoai ngu", "Cong nghe thong tin", "Kinh te"
    );
    private static final List<String> VN_QUESTION_TITLES = Arrays.asList(
            "Thong tin nao sau day la dung",
            "Chon dap an chinh xac",
            "Noi dung nao sau day la sai",
            "Dau la vi du phu hop",
            "Y nghia cua khai niem nay la gi"
    );
    private static final List<String> VN_ANSWERS = Arrays.asList(
            "Lua chon A", "Lua chon B", "Lua chon C", "Lua chon D"
    );
    private static final List<String> VN_QUIZ_TITLES = Arrays.asList(
            "Kiem tra giua ky", "On tap cuoi ky", "Kiem tra nhanh", "Kiem tra tong hop", "De thi thu"
    );
    private static final List<String> VN_DESCRIPTIONS = Arrays.asList(
            "Bo cau hoi on tap theo chu de.",
            "Danh gia kien thuc co ban.",
            "Bai kiem tra ngan de luyen tap.",
            "Tong hop kien thuc trong chuong.",
            "De thi thu theo cau truc de chinh thuc."
    );
    private static final List<String> VN_NUMBERING = Arrays.asList("numeric", "roman", "alphabet");

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data seeding...");

        List<Subject> subjects = subjectRepo.count() > 0
                ? subjectRepo.findAll()
                : seedSubjects(SUBJECT_COUNT);
        List<User> users = userRepo.count() > 0
                ? userRepo.findAll()
                : seedUsers(USER_COUNT);
        // Always ensure load-test users exist and have known credentials
        users = ensureLoadTestUsers(users, USER_COUNT);
        seedQuestionSystem(users);

        List<Quiz> quizzes = quizRepo.count() > 0
                ? new ArrayList<>(quizRepo.findAll())
                : new ArrayList<>();
        if (!users.isEmpty() && quizzes.size() < QUIZ_COUNT) {
            quizzes.addAll(seedQuizzes(subjects, users, QUIZ_COUNT - quizzes.size()));
        }

        User loadTestHost = !users.isEmpty()
                ? users.get(0)
                : userRepo.findByEmail(loadTestEmail(1)).orElse(null);
        if (loadTestHost != null) {
            ensureLoadTestQuiz(subjects, loadTestHost);
        }
        quizzes = quizRepo.findAll();

        if (!users.isEmpty() && !quizzes.isEmpty()) {
            long lobbyCount = lobbyRepo.count();
            int missingLobbies = (int) Math.max(0, LOBBY_COUNT - lobbyCount);
            if (missingLobbies > 0) {
                seedLobbies(users, quizzes, missingLobbies);
            }
        }

        if (!users.isEmpty()) {
            ensureLoadTestLobby(users, subjects);
        }

        quizzes = quizRepo.findAll();
        List<Lobby> lobbies = lobbyRepo.count() > 0 ? lobbyRepo.findAll() : Collections.emptyList();
        seedNotifications(users, lobbies);
        seedQuizInstancesAndResponses(users, quizzes);

        log.info("Data seeding completed successfully!");
    }

    private List<Subject> seedSubjects(int count) {
        List<Subject> subjects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Subject subject = new Subject();
            subject.setSubjectName(VN_SUBJECTS.get(i % VN_SUBJECTS.size()));
            subject.setDescription(pickRandom(VN_DESCRIPTIONS));
            subjects.add(subjectRepo.save(subject));
        }
        return subjects;
    }

    private List<User> seedUsers(int count) {
        Role userRole = roleRepo.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new RuntimeException("USER_ROLE not found"));

        List<User> users = new ArrayList<>();
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

        Set<String> usedUsernames = new HashSet<>();
        Set<String> usedEmails = new HashSet<>();

        for (int i = 1; i <= count; i++) {
            String userName = "user" + i;
            String email = loadTestEmail(i);

            if (userRepo.existsByEmail(email)) {
                continue;
            }

            User user = new User();
            user.setUserName(uniqueValue(userName, usedUsernames));
            user.setEmail(uniqueValue(email, usedEmails));
            user.setLastLoginAt(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30)));
            user.setProfilePictureUrl(faker.internet().avatar());
            user.setPassword(encodedPassword);
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            user.setEmailVerified(true);
            users.add(userRepo.save(user));
        }
        return users;
    }

    private List<User> ensureLoadTestUsers(List<User> existingUsers, int count) {
        Role userRole = roleRepo.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new RuntimeException("USER_ROLE not found"));

        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
        List<User> ensured = new ArrayList<>(existingUsers);

        for (int i = 1; i <= count; i++) {
            String email = loadTestEmail(i);
            Optional<User> found = userRepo.findByEmail(email);
            if (found.isPresent()) {
                User user = found.get();
                if (!user.isEmailVerified()) {
                    user.setEmailVerified(true);
                }
                if (user.getRoles() == null || user.getRoles().isEmpty()) {
                    user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
                }
                user.setPassword(encodedPassword);
                ensured.add(userRepo.save(user));
                continue;
            }

            User user = new User();
            user.setUserName("user" + i);
            user.setEmail(email);
            user.setLastLoginAt(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30)));
            user.setProfilePictureUrl(faker.internet().avatar());
            user.setPassword(encodedPassword);
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            user.setEmailVerified(true);
            ensured.add(userRepo.save(user));
        }

        return ensured;
    }

    private void seedQuestionSystem(List<User> users) {
        for (User user : users) {
            if (user.getId() == null) {
                continue;
            }
            QuestionBank bank = questionBankRepo.findByOwnerId(user.getId())
                    .orElseGet(() -> {
                        QuestionBank newBank = new QuestionBank();
                        newBank.setOwner(user);
                        return questionBankRepo.save(newBank);
                    });

            List<Question> existing = questionRepo.findByBankId(bank.getId());
            if (existing != null && !existing.isEmpty()) {
                continue;
            }

            for (int i = 0; i < FOLDERS_PER_USER; i++) {
                Folder folder = new Folder();
                folder.setName(pickRandom(VN_DEPARTMENTS));
                folder.setBank(bank);
                folder = folderRepo.save(folder);

                seedQuestions(bank, folder, QUESTIONS_PER_FOLDER);
            }

            seedQuestions(bank, null, QUESTIONS_PER_BANK);
        }
    }

    private void seedQuestions(QuestionBank bank, Folder folder, int count) {
        for (int i = 0; i < count; i++) {
            Question question = new Question();
            question.setQuestionName(pickRandom(VN_QUESTION_TITLES) + "?");
            question.setType(ContentType.TEXT);
            question.setAnswerType(AnswerType.SINGLE_CHOICE);
            question.setLevel(Level.values()[faker.random().nextInt(Level.values().length)]);
            question.setBank(bank);
            question.setFolder(folder);
            question.setContentHash(questionService.generateContentHash(question));
            question = questionRepo.save(question);

            seedAnswers(question);
        }
    }

    private void seedAnswers(Question question) {
        int correctIdx = faker.random().nextInt(4);
        for (int i = 0; i < 4; i++) {
            Answer answer = new Answer();
            answer.setAnswerName(VN_ANSWERS.get(i));
            answer.setType(ContentType.TEXT);
            answer.setAnswerCorrect(i == correctIdx);
            answer.setQuestion(question);
            answerRepo.save(answer);
        }
    }

    private List<Quiz> seedQuizzes(List<Subject> subjects, List<User> users, int count) {
        List<Question> allQuestions = questionRepo.findAll();
        if (allQuestions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Quiz> quizzes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Quiz quiz = new Quiz();
            quiz.setTitle(pickRandom(VN_QUIZ_TITLES));
            quiz.setDescription(pickRandom(VN_DESCRIPTIONS));
            quiz.setSubject(subjects.get(faker.random().nextInt(subjects.size())));
            quiz.setHost(users.get(faker.random().nextInt(users.size())));
            quiz.setTimeLimitMinutes(faker.random().nextInt(10, 60));
            QuizStatus status = pickQuizStatus();
            quiz.setStatus(status);
            quiz.setVisibility(pickQuizVisibility());
            applyQuizSchedule(quiz, status);
            quiz.setMaxAttempt(faker.random().nextInt(1, 10));

            QuizConfig config = new QuizConfig();
            config.setShuffleQuestions(faker.bool().bool());
            config.setShuffleAnswers(faker.bool().bool());
            config.setAutoDistributePoints(faker.bool().bool());
            config.setShowScoreImmediately(faker.bool().bool());
            config.setAllowReview(true);
            config.setPassingScore(faker.number().numberBetween(50, 90));
            quiz.setConfig(config);

            QuestionLayout layout = new QuestionLayout();
            layout.setQuestionNumbering(pickRandom(VN_NUMBERING));
            layout.setQuestionPerPage(faker.random().nextInt(1, 6));
            layout.setAnswerPerRow(faker.random().nextInt(1, 3));
            quiz.setQuestionLayout(layout);

            quiz = quizRepo.save(quiz);
            quizzes.add(quiz);

            Set<Question> selectedQuestions = pickRandomQuestions(allQuestions, faker.random().nextInt(5, 15));
            List<QuizQuestionLink> links = new ArrayList<>();

            for (Question q : selectedQuestions) {
                QuizQuestionLink link = new QuizQuestionLink();
                link.setId(new QuizQuestionId(quiz.getId(), q.getId()));
                link.setQuiz(quiz);
                link.setQuestion(q);
                link.setPoints(faker.number().numberBetween(1L, 10L));
                links.add(link);
            }

            quizQuestionLinkRepo.saveAll(links);
            quiz.setTotalQuestion(selectedQuestions.size());
            if (quiz.getVisibility() == QuizVisibility.PUBLIC) {
                quiz.setLobby(null);
            }
            quizRepo.save(quiz);
        }
        return quizzes;
    }

    private void ensureLoadTestQuiz(List<Subject> subjects, User host) {
        if (subjects.isEmpty() || host == null) {
            return;
        }

        Long hostId = host.getId();
        if (hostId == null || quizRepo.existsByTitleAndHostId(LOAD_TEST_QUIZ_TITLE, hostId)) {
            return;
        }

        List<Question> allQuestions = questionRepo.findAll();
        if (allQuestions.isEmpty()) {
            return;
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(LOAD_TEST_QUIZ_TITLE);
        quiz.setDescription("Quiz for load testing");
        quiz.setSubject(subjects.get(0));
        quiz.setHost(host);
        quiz.setTimeLimitMinutes(30);
        quiz.setStatus(QuizStatus.OPENED);
        quiz.setVisibility(QuizVisibility.PUBLIC);
        quiz.setStartDate(LocalDateTime.now());
        quiz.setEndDate(LocalDateTime.now().plusDays(7));
        quiz.setMaxAttempt(3);

        QuizConfig config = new QuizConfig();
        config.setShuffleQuestions(true);
        config.setShuffleAnswers(true);
        config.setAutoDistributePoints(true);
        config.setShowScoreImmediately(false);
        config.setAllowReview(true);
        config.setPassingScore(60);
        quiz.setConfig(config);

        QuestionLayout layout = new QuestionLayout();
        layout.setQuestionNumbering("numeric");
        layout.setQuestionPerPage(1);
        layout.setAnswerPerRow(1);
        quiz.setQuestionLayout(layout);

        quiz = quizRepo.save(quiz);

        Set<Question> selectedQuestions = pickRandomQuestions(allQuestions, Math.min(10, allQuestions.size()));
        List<QuizQuestionLink> links = new ArrayList<>();
        for (Question q : selectedQuestions) {
            QuizQuestionLink link = new QuizQuestionLink();
            link.setId(new QuizQuestionId(quiz.getId(), q.getId()));
            link.setQuiz(quiz);
            link.setQuestion(q);
            link.setPoints(5L);
            links.add(link);
        }
        quizQuestionLinkRepo.saveAll(links);
        quiz.setTotalQuestion(selectedQuestions.size());
        quiz.setLobby(null);
        quizRepo.save(quiz);
    }

    private void ensureLoadTestLobby(List<User> users, List<Subject> subjects) {
        if (users.isEmpty() || subjects.isEmpty()) {
            return;
        }

        if (lobbyRepo.existsByLobbyName(LOAD_TEST_LOBBY_NAME)) {
            return;
        }

        User host = users.get(0);
        Lobby lobby = new Lobby();
        lobby.setLobbyName(LOAD_TEST_LOBBY_NAME);
        lobby.setHost(host);
        lobby.setCodeInvite(faker.number().digits(6));
        Lobby savedLobby = lobbyRepo.save(lobby);

        Set<LobbyMember> members = new HashSet<>();
        for (User user : users) {
            LobbyMemberId id = new LobbyMemberId(savedLobby.getId(), user.getId());
            LobbyRole role = user.getId().equals(host.getId()) ? LobbyRole.HOST : LobbyRole.MEMBER;
            LobbyMember member = LobbyMember.builder()
                    .id(id)
                    .lobby(savedLobby)
                    .user(user)
                    .role(role)
                    .joinedAt(LocalDateTime.now())
                    .build();
            members.add(member);
        }
        savedLobby.setMembers(members);
        lobbyRepo.save(savedLobby);

        List<Question> allQuestions = questionRepo.findAll();
        if (allQuestions.isEmpty()) {
            return;
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(LOAD_TEST_GROUP_QUIZ_TITLE);
        quiz.setDescription("Group quiz for load testing");
        quiz.setSubject(subjects.get(0));
        quiz.setHost(host);
        quiz.setTimeLimitMinutes(30);
        quiz.setStatus(QuizStatus.OPENED);
        quiz.setVisibility(QuizVisibility.CLASS_ONLY);
        quiz.setStartDate(LocalDateTime.now());
        quiz.setEndDate(LocalDateTime.now().plusDays(7));
        quiz.setMaxAttempt(10);
        quiz.setLobby(savedLobby);

        QuizConfig config = new QuizConfig();
        config.setShuffleQuestions(true);
        config.setShuffleAnswers(true);
        config.setAutoDistributePoints(true);
        config.setShowScoreImmediately(false);
        config.setAllowReview(true);
        config.setPassingScore(60);
        quiz.setConfig(config);

        QuestionLayout layout = new QuestionLayout();
        layout.setQuestionNumbering("numeric");
        layout.setQuestionPerPage(1);
        layout.setAnswerPerRow(1);
        quiz.setQuestionLayout(layout);

        quiz = quizRepo.save(quiz);

        Set<Question> selectedQuestions = pickRandomQuestions(allQuestions, Math.min(10, allQuestions.size()));
        List<QuizQuestionLink> links = new ArrayList<>();
        for (Question q : selectedQuestions) {
            QuizQuestionLink link = new QuizQuestionLink();
            link.setId(new QuizQuestionId(quiz.getId(), q.getId()));
            link.setQuiz(quiz);
            link.setQuestion(q);
            link.setPoints(5L);
            links.add(link);
        }
        quizQuestionLinkRepo.saveAll(links);
        quiz.setTotalQuestion(selectedQuestions.size());
        quizRepo.save(quiz);
    }

    private void seedLobbies(List<User> users, List<Quiz> quizzes, int count) {
        if (quizzes.isEmpty()) {
            return;
        }

        List<Quiz> quizPool = quizzes.stream()
                .filter(q -> q.getVisibility() != QuizVisibility.PUBLIC)
                .collect(Collectors.toList());
        Collections.shuffle(quizPool);
        int quizIndex = 0;

        for (int i = 0; i < count; i++) {
            Lobby lobby = new Lobby();
            User host = users.get(faker.random().nextInt(users.size()));
            lobby.setLobbyName("Phong thi so " + (i + 1));
            lobby.setHost(host);
            lobby.setCodeInvite(faker.number().digits(6));

            Lobby savedLobby = lobbyRepo.save(lobby);

            Set<LobbyMember> members = new HashSet<>();
            LobbyMemberId hostId = new LobbyMemberId(savedLobby.getId(), host.getId());
            LobbyMember hostMember = LobbyMember.builder()
                    .id(hostId)
                    .lobby(savedLobby)
                    .user(host)
                    .role(LobbyRole.HOST)
                    .joinedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 14)))
                    .build();
            members.add(hostMember);

            List<User> shuffledUsers = new ArrayList<>(users);
            Collections.shuffle(shuffledUsers);
            int maxExtra = Math.max(0, shuffledUsers.size() - 1);
            int numMembers = maxExtra == 0 ? 0 : faker.random().nextInt(1, Math.min(6, maxExtra) + 1);

            List<User> selectedUsers = shuffledUsers.stream()
                    .filter(u -> !u.getId().equals(host.getId()))
                    .limit(numMembers)
                    .collect(Collectors.toList());

            members.addAll(selectedUsers.stream()
                    .map(user -> {
                        LobbyMemberId id = new LobbyMemberId(savedLobby.getId(), user.getId());
                        return LobbyMember.builder()
                                .id(id)
                                .lobby(savedLobby)
                                .user(user)
                                .role(LobbyRole.MEMBER)
                                .joinedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 14)))
                                .build();
                    })
                    .collect(Collectors.toSet()));
            savedLobby.setMembers(members);

            int remainingQuizzes = quizPool.size() - quizIndex;
            int quizzesForLobby = remainingQuizzes > 0 ? Math.min(2, remainingQuizzes) : 0;

            for (int q = 0; q < quizzesForLobby; q++) {
                Quiz quiz = quizPool.get(quizIndex++);
                quiz.setLobby(savedLobby);
                savedLobby.getQuizzes().add(quiz);
                quizRepo.save(quiz);
            }

            lobbyRepo.save(savedLobby);
        }
    }

    private Set<Question> pickRandomQuestions(List<Question> allQuestions, int count) {
        Set<Question> selected = new HashSet<>();
        if (allQuestions.isEmpty() || count <= 0) {
            return selected;
        }

        for (int i = 0; i < count; i++) {
            selected.add(allQuestions.get(faker.random().nextInt(allQuestions.size())));
        }
        return selected;
    }

    private String pickRandom(List<String> values) {
        return values.get(faker.random().nextInt(values.size()));
    }

    private QuizVisibility pickQuizVisibility() {
        return faker.random().nextDouble() < PUBLIC_QUIZ_RATIO
                ? QuizVisibility.PUBLIC
                : QuizVisibility.CLASS_ONLY;
    }

    private QuizStatus pickQuizStatus() {
        double roll = faker.random().nextDouble();
        if (roll < 0.6) {
            return QuizStatus.OPENED;
        }
        if (roll < 0.75) {
            return QuizStatus.DRAFT;
        }
        if (roll < 0.85) {
            return QuizStatus.CLOSED;
        }
        if (roll < 0.93) {
            return QuizStatus.PAUSED;
        }
        return QuizStatus.ARCHIVED;
    }

    private void applyQuizSchedule(Quiz quiz, QuizStatus status) {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case DRAFT -> {
                LocalDateTime start = now.plusDays(faker.number().numberBetween(1, 14));
                quiz.setStartDate(start);
                quiz.setEndDate(start.plusDays(faker.number().numberBetween(1, 14)));
            }
            case CLOSED, ARCHIVED -> {
                LocalDateTime end = now.minusDays(faker.number().numberBetween(1, 14));
                quiz.setEndDate(end);
                quiz.setStartDate(end.minusDays(faker.number().numberBetween(1, 14)));
            }
            default -> {
                quiz.setStartDate(now.minusDays(faker.number().numberBetween(0, 2)));
                quiz.setEndDate(now.plusDays(faker.number().numberBetween(3, 14)));
            }
        }
    }

    private String makeUsername(String firstName, String lastName) {
        String temp = Normalizer.normalize(firstName + lastName, Normalizer.Form.NFD);
        String base = temp.replaceAll("\\p{M}", "").replace(" ", "").toLowerCase();
        return base.isBlank() ? "user" : base;
    }

    private String uniqueValue(String base, Set<String> used) {
        String candidate = base;
        int counter = 1;
        while (!used.add(candidate)) {
            candidate = base + counter;
            counter++;
        }
        return candidate;
    }

    private String loadTestEmail(int index) {
        return "user" + index + "@" + LOAD_TEST_EMAIL_DOMAIN;
    }

    private void seedNotifications(List<User> users, List<Lobby> lobbies) {
        if (users.isEmpty() || notificationRepo.count() > 0) {
            return;
        }

        User host = users.get(0);
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < NOTIFICATION_COUNT; i++) {
            Notification notification = new Notification();
            notification.setTitle("Thong bao he thong " + (i + 1));
            notification.setContent("Noi dung thong bao mau " + (i + 1));
            notification.setType(NotificationType.SYSTEM);
            notification.setHost(host);
            notifications.add(notificationRepo.save(notification));
        }

        if (!lobbies.isEmpty()) {
            for (int i = 0; i < Math.min(3, lobbies.size()); i++) {
                Lobby lobby = lobbies.get(i);
                Notification notification = new Notification();
                notification.setTitle("Thong bao lop " + (i + 1));
                notification.setContent("Thong bao moi trong nhom " + lobby.getLobbyName());
                notification.setType(NotificationType.GROUP);
                notification.setLobby(lobby);
                notification.setHost(lobby.getHost() != null ? lobby.getHost() : host);
                notifications.add(notificationRepo.save(notification));
            }
        }

        List<User> targetUsers = users.size() > 200 ? users.subList(0, 200) : users;
        List<UserNotification> links = new ArrayList<>();
        for (Notification notification : notifications) {
            List<User> recipients;
            if (notification.getType() == NotificationType.GROUP && notification.getLobby() != null) {
                recipients = notification.getLobby().getMembers().stream()
                        .map(LobbyMember::getUser)
                        .collect(Collectors.toList());
                if (recipients.isEmpty()) {
                    recipients = targetUsers;
                }
            } else {
                recipients = targetUsers;
            }
            for (User user : recipients) {
                NotificationUserId id = new NotificationUserId(user.getId(), notification.getId());
                UserNotification link = new UserNotification();
                link.setId(id);
                link.setUser(user);
                link.setNotification(notification);
                link.setRead(faker.bool().bool());
                link.setReadAt(faker.bool().bool()
                        ? LocalDateTime.now().minusDays(faker.number().numberBetween(0, 7))
                        : null);
                links.add(link);
            }
        }
        notificationUserRepo.saveAll(links);
    }

    private void seedQuizInstancesAndResponses(List<User> users, List<Quiz> quizzes) {
        if (users.isEmpty() || quizzes.isEmpty()) {
            return;
        }
        if (quizInstanceRepo.count() > 0 && quizUserResponseRepo.count() > 0) {
            return;
        }

        List<User> sampleUsers = users.size() > QUIZ_INSTANCE_SEED_USERS
                ? users.subList(0, QUIZ_INSTANCE_SEED_USERS)
                : users;
        List<Quiz> quizPool = quizzes.stream()
                .filter(q -> q.getQuizQuestionLinks() != null && !q.getQuizQuestionLinks().isEmpty())
                .limit(QUIZ_INSTANCE_SEED_QUIZZES)
                .collect(Collectors.toList());
        if (quizPool.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int userIdx = 0;

        for (Quiz quiz : quizPool) {
            QuizQuestionSnapshot snapshot = buildSnapshot(quiz);
            if (snapshot.getQuestions() == null || snapshot.getQuestions().isEmpty()) {
                continue;
            }

            for (QuizInstanceStatus status : List.of(
                    QuizInstanceStatus.IN_PROGRESS,
                    QuizInstanceStatus.SUBMITTED,
                    QuizInstanceStatus.TIMED_OUT)) {
                User user = sampleUsers.get(userIdx++ % sampleUsers.size());
                LocalDateTime startedAt = now.minusMinutes(faker.number().numberBetween(5, 300));

                QuizInstance instance = new QuizInstance();
                instance.setQuiz(quiz);
                instance.setUser(user);
                instance.setGuestId(null);
                instance.setStartedAt(startedAt);
                instance.setStatus(status);
                instance.setSnapshot(snapshot);
                instance.setTotalPoints(snapshot.getTotalPoints());

                if (status != QuizInstanceStatus.IN_PROGRESS) {
                    instance.setEndedAt(startedAt.plusMinutes(faker.number().numberBetween(5, 120)));
                }

                instance = quizInstanceRepo.save(instance);
                List<QuizUserResponse> responses = buildResponses(instance, snapshot, status, startedAt);
                quizUserResponseRepo.saveAll(responses);

                if (status != QuizInstanceStatus.IN_PROGRESS) {
                    long earned = responses.stream()
                            .filter(QuizUserResponse::isCorrect)
                            .mapToLong(QuizUserResponse::getPointsEarned)
                            .sum();
                    instance.setEarnedPoints(earned);
                    quizInstanceRepo.save(instance);
                }
            }
        }
    }

    private QuizQuestionSnapshot buildSnapshot(Quiz quiz) {
        QuizQuestionSnapshot snapshot = new QuizQuestionSnapshot();
        snapshot.setQuizId(quiz.getId());
        snapshot.setGeneratedAt(LocalDateTime.now());
        snapshot.setVersion(1);
        snapshot.setDuration(quiz.getTimeLimitMinutes());
        boolean shuffleQuestions = quiz.getConfig() != null
                && Boolean.TRUE.equals(quiz.getConfig().getShuffleQuestions());
        boolean shuffleAnswers = quiz.getConfig() != null
                && Boolean.TRUE.equals(quiz.getConfig().getShuffleAnswers());
        snapshot.setShuffledQuestions(shuffleQuestions);
        snapshot.setShuffledAnswers(shuffleAnswers);

        List<QuizQuestionLink> links = new ArrayList<>(quiz.getQuizQuestionLinks());
        if (shuffleQuestions) {
            Collections.shuffle(links);
        } else {
            links.sort(Comparator.comparing(link -> link.getQuestion().getId()));
        }

        List<QuestionSnapshot> questionSnapshots = new ArrayList<>();
        long totalPoints = 0L;

        for (int i = 0; i < links.size(); i++) {
            QuizQuestionLink link = links.get(i);
            Question question = link.getQuestion();
            if (question == null) {
                continue;
            }

            QuestionSnapshot qs = new QuestionSnapshot();
            qs.setKey(UUID.randomUUID().toString());
            qs.setContent(question.getQuestionName());
            qs.setType(question.getType());
            qs.setAnswerType(question.getAnswerType());
            qs.setOrderIndex(i + 1);
            long points = link.getPoints() != null ? link.getPoints() : 1L;
            qs.setPoints(points);
            totalPoints += points;

            List<Answer> answers = new ArrayList<>(question.getAnswers());
            if (shuffleAnswers) {
                Collections.shuffle(answers);
            } else {
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
                    correctAnsIds.add(String.valueOf(j));
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

    private List<QuizUserResponse> buildResponses(
            QuizInstance instance,
            QuizQuestionSnapshot snapshot,
            QuizInstanceStatus status,
            LocalDateTime startedAt) {
        List<QuizUserResponse> responses = new ArrayList<>();
        for (int i = 0; i < snapshot.getQuestions().size(); i++) {
            QuestionSnapshot qs = snapshot.getQuestions().get(i);
            boolean skipped = status == QuizInstanceStatus.IN_PROGRESS && faker.random().nextInt(100) < 30;
            if (qs.getAnswers() == null || qs.getAnswers().isEmpty()) {
                skipped = true;
            }
            List<Long> selected = new ArrayList<>();
            boolean isCorrect = false;

            if (!skipped && qs.getAnswers() != null && !qs.getAnswers().isEmpty()) {
                List<Integer> correctIndexes = qs.getCorrectAnswerIds().stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                boolean chooseCorrect = faker.random().nextInt(100) < 60;
                if (chooseCorrect && !correctIndexes.isEmpty()) {
                    selected = correctIndexes.stream().map(Integer::longValue).collect(Collectors.toList());
                    isCorrect = true;
                } else {
                    int answerCount = qs.getAnswers().size();
                    int wrongIndex = answerCount > 1
                            ? pickWrongIndex(answerCount, new HashSet<>(correctIndexes))
                            : 0;
                    selected = List.of((long) wrongIndex);
                    isCorrect = false;
                }
            }

            QuizUserResponse response = new QuizUserResponse();
            response.setQuizInstance(instance);
            response.setQuestionId(null);
            response.setQuestionSnapshotKey(qs.getKey());
            response.setSelectedAnswerIds(selected);
            response.setSelectedAnswerId(selected.isEmpty() ? null : selected.get(0));
            response.setCorrect(isCorrect);
            response.setPointsEarned(isCorrect ? qs.getPoints() : 0L);
            response.setResponseTimeSeconds(faker.number().numberBetween(3, 400));
            response.setAnsweredAt(startedAt.plusSeconds(faker.number().numberBetween(5, 600)));
            response.setSkipped(skipped);
            responses.add(response);
        }
        return responses;
    }

    private int pickWrongIndex(int total, Set<Integer> correct) {
        if (total <= 1) {
            return 0;
        }
        int idx;
        do {
            idx = faker.random().nextInt(total);
        } while (correct.contains(idx));
        return idx;
    }
}
