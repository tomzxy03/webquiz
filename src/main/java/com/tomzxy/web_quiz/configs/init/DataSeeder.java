package com.tomzxy.web_quiz.configs.init;

import com.github.javafaker.Faker;
import com.tomzxy.web_quiz.containts.PredefinedRole;
import com.tomzxy.web_quiz.enums.*;
import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.models.Host.LobbyMemberId;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizConfig;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionId;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.Quiz.QuestionLayout;
import com.tomzxy.web_quiz.models.User.User;
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
    private final PasswordEncoder passwordEncoder;
    private final QuestionService questionService;

    private final Faker faker = new Faker(new Locale("vi"));
    private static final String DEFAULT_PASSWORD = "123456";

    private static final int SUBJECT_COUNT = 10;
    private static final int USER_COUNT = 2000;
    private static final int QUIZ_COUNT = 40;
    private static final int LOBBY_COUNT = 20;
    private static final int FOLDERS_PER_USER = 3;
    private static final int QUESTIONS_PER_FOLDER = 6;
    private static final int QUESTIONS_PER_BANK = 6;
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
        Optional<User> loadTestUser = userRepo.findByEmail(loadTestEmail(1));
        boolean loadTestQuizReady = loadTestUser.isPresent()
                && quizRepo.existsByTitleAndHostId(LOAD_TEST_QUIZ_TITLE, loadTestUser.get().getId());
        boolean loadTestGroupReady = loadTestUser.isPresent()
                && lobbyRepo.existsByLobbyName(LOAD_TEST_LOBBY_NAME)
                && quizRepo.existsByTitleAndHostId(LOAD_TEST_GROUP_QUIZ_TITLE, loadTestUser.get().getId());
        if (loadTestQuizReady && loadTestGroupReady) {
            log.info("Load test data already seeded, skipping...");
            return;
        }

        log.info("Starting data seeding...");

        List<Subject> subjects = subjectRepo.count() > 0
                ? subjectRepo.findAll()
                : seedSubjects(SUBJECT_COUNT);
        List<User> users = userRepo.count() > 0
                ? userRepo.findAll()
                : seedUsers(USER_COUNT);
        // Always ensure load-test users exist and have known credentials
        users = ensureLoadTestUsers(users, USER_COUNT);

        if (!loadTestQuizReady) {
            seedQuestionSystem(users);
            List<Quiz> quizzes = users.isEmpty()
                    ? Collections.emptyList()
                    : seedQuizzes(subjects, users, QUIZ_COUNT);
            User loadTestHost = !users.isEmpty()
                    ? users.get(0)
                    : userRepo.findByEmail(loadTestEmail(1)).orElse(null);
            if (loadTestHost != null) {
                ensureLoadTestQuiz(subjects, loadTestHost);
            }
            if (!users.isEmpty() && !quizzes.isEmpty()) {
                seedLobbies(users, quizzes, LOBBY_COUNT);
            }
        }

        if (!users.isEmpty()) {
            ensureLoadTestLobby(users, subjects);
        }

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
            QuestionBank bank = new QuestionBank();
            bank.setOwner(user);
            bank = questionBankRepo.save(bank);

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
            quiz.setStatus(QuizStatus.OPENED);
            quiz.setVisibility(pickQuizVisibility());
            quiz.setStartDate(LocalDateTime.now());
            quiz.setEndDate(LocalDateTime.now().plusDays(7));
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
}
