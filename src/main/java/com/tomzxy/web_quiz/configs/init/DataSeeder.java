package com.tomzxy.web_quiz.configs.init;

import com.github.javafaker.Faker;
import com.tomzxy.web_quiz.containts.PredefinedRole;
import com.tomzxy.web_quiz.enums.*;
import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.models.Host.LobbyMemberId;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionId;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    private final Faker faker = new Faker();
    private static final String DEFAULT_PASSWORD = "password123";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepo.count() > 2) {
            log.info("Data already seeded, skipping...");
            return;
        }

        log.info("Starting data seeding...");

        // 1. Seed Subjects
        List<Subject> subjects = seedSubjects(10);

        // 2. Seed Users
        List<User> users = seedUsers(20);

        // 3. Seed QuestionBanks, Folders, Questions, Answers
        seedQuestionSystem(users);

        // 4. Seed Quizzes
        seedQuizzes(subjects, users, 20);

        // 5. Seed Lobbies
        seedLobbies(users, 10);

        log.info("Data seeding completed successfully!");
    }

    private List<Subject> seedSubjects(int count) {
        List<Subject> subjects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Subject subject = new Subject();
            subject.setSubjectName(faker.educator().course());
            subject.setDescription(faker.lorem().sentence());
            subjects.add(subjectRepo.save(subject));
        }
        return subjects;
    }

    private List<User> seedUsers(int count) {
        Role userRole = roleRepo.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new RuntimeException("USER_ROLE not found"));

        List<User> users = new ArrayList<>();
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUserName(faker.name().username().replace(" ", ""));
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(encodedPassword);
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            user.setEmailVerified(true);
            users.add(userRepo.save(user));
        }
        return users;
    }

    private void seedQuestionSystem(List<User> users) {
        for (User user : users) {
            // Each user has one QuestionBank
            QuestionBank bank = new QuestionBank();
            bank.setOwner(user);
            bank = questionBankRepo.save(bank);

            // Create some folders for the user
            for (int i = 0; i < 3; i++) {
                Folder folder = new Folder();
                folder.setName(faker.commerce().department());
                folder.setBank(bank);
                folder = folderRepo.save(folder);

                // Add questions to folder
                seedQuestions(bank, folder, 5);
            }

            // Add some questions directly to bank (no folder)
            seedQuestions(bank, null, 5);
        }
    }

    private void seedQuestions(QuestionBank bank, Folder folder, int count) {
        for (int i = 0; i < count; i++) {
            Question question = new Question();
            question.setQuestionName(faker.lorem().sentence() + "?");
            question.setQuestionType(QuestionAndAnswerType.TEXT);
            question.setLevel(Level.values()[faker.random().nextInt(Level.values().length)]);
            question.setBank(bank);
            question.setFolder(folder);
            question.setContentHash(UUID.randomUUID().toString());
            question = questionRepo.save(question);

            seedAnswers(question);
        }
    }

    private void seedAnswers(Question question) {
        int correctIdx = faker.random().nextInt(4);
        for (int i = 0; i < 4; i++) {
            Answer answer = new Answer();
            answer.setAnswerName(faker.lorem().sentence());
            answer.setAnswerType(QuestionAndAnswerType.TEXT);
            answer.setAnswerCorrect(i == correctIdx);
            answer.setQuestion(question);
            answerRepo.save(answer);
        }
    }

    private void seedQuizzes(List<Subject> subjects, List<User> users, int count) {
        List<Question> allQuestions = questionRepo.findAll();
        if (allQuestions.isEmpty())
            return;

        for (int i = 0; i < count; i++) {
            Quiz quiz = new Quiz();
            quiz.setTitle(faker.book().title());
            quiz.setDescription(faker.lorem().paragraph());
            quiz.setSubject(subjects.get(faker.random().nextInt(subjects.size())));
            quiz.setHost(users.get(faker.random().nextInt(users.size())));
            quiz.setTimeLimitMinutes(faker.random().nextInt(10, 60));
            quiz.setStatus(QuizStatus.OPENED);
            quiz.setVisibility(QuizVisibility.PUBLIC);
            quiz.setStartDate(LocalDateTime.now());
            quiz.setEndDate(LocalDateTime.now().plusDays(7));
            quiz = quizRepo.save(quiz);

            // Link some random questions to this quiz
            Set<Question> selectedQuestions = new HashSet<>();
            int numQuest = faker.random().nextInt(5, 15);
            for (int j = 0; j < numQuest; j++) {
                selectedQuestions.add(allQuestions.get(faker.random().nextInt(allQuestions.size())));
            }

            for (Question q : selectedQuestions) {
                QuizQuestionLink link = new QuizQuestionLink();
                link.setId(new QuizQuestionId(quiz.getId(), q.getId()));
                link.setQuiz(quiz);
                link.setQuestion(q);
                link.setPoints(faker.number().numberBetween(1L, 10L));
                quizQuestionLinkRepo.save(link);
            }
        }
    }

    private void seedLobbies(List<User> users, int count) {
        List<Quiz> quizzes = quizRepo.findAll();
        if (quizzes.isEmpty()) return;

        for (int i = 0; i < count; i++) {

            Quiz quiz = quizzes.get(i % quizzes.size());

            Lobby lobby = new Lobby();
            lobby.setLobbyName("Lobby for " + quiz.getTitle());
            lobby.setHost(quiz.getHost());
            lobby.setCodeInvite(faker.number().digits(6));

            // save lobby trước
            Lobby savedLobby = lobbyRepo.save(lobby);

            Collections.shuffle(users);
            int numMembers = faker.random().nextInt(3, 10);

            List<User> selectedUsers = users.stream()
                    .limit(numMembers)
                    .toList();

            Set<LobbyMember> members = selectedUsers.stream()
                    .map(user -> {

                        LobbyMemberId id = new LobbyMemberId(
                                savedLobby.getId(),
                                user.getId()
                        );

                        return LobbyMember.builder()
                                .id(id)
                                .lobby(savedLobby)
                                .user(user)
                                .build();
                    })
                    .collect(Collectors.toSet());

            savedLobby.setMembers(members);

            savedLobby.getQuizzes().add(quiz);
            quiz.setLobby(savedLobby);

            lobbyRepo.save(savedLobby);
            quizRepo.save(quiz);
        }
    }
}
