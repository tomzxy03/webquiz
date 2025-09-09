package com.tomzxy.web_quiz.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizSubmissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.*;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.enums.QuizOptions;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizInstance;
import com.tomzxy.web_quiz.models.Quiz.QuizUserResponse;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import com.tomzxy.web_quiz.mapstructs.QuizInstanceMapper;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
import com.tomzxy.web_quiz.mapstructs.AnswerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final ObjectMapper objectMapper;
    private final QuizInstanceMapper quizInstanceMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final QuizQuestionRepo quizQuestionRepo;
    private final QuizAnswerRepo quizAnswerRepo;
    private final ConvertToPageResDTO convertToPageResDTO;

    @Override
    public QuizInstanceResDTO createQuizInstance(QuizInstanceReqDTO request) {
        log.info("Creating quiz instance for quiz: {}", request.getQuizId());

        Quiz quiz = quizRepo.findById(request.getQuizId())
                .orElseThrow(() -> new NotFoundException("Quiz không tồn tại"));
        
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));

        // kiểm tra user da qua so lan tham gia quiz
        if (!canUserStartQuiz(request.getQuizId(), request.getUserId())) {
            throw new RuntimeException("User không có quyền tham gia quiz này");
        }

        boolean shuffleQuestions = request.getOptions() != null && 
                request.getOptions().contains(QuizOptions.SHUFFLE_QUESTIONS);
        boolean shuffleAnswers = request.getOptions() != null && 
                request.getOptions().contains(QuizOptions.SHUFFLE_ANSWERS);

        QuizInstance instance = QuizInstance.builder()
                .quiz(quiz)
                .user(user)
                .startedAt(LocalDateTime.now())
                .shuffleQuestions(shuffleQuestions)
                .shuffleAnswers(shuffleAnswers)
                .status(QuizInstanceStatus.IN_PROGRESS)
                .build();

        instance = quizInstanceRepo.save(instance);

        List<Question> questions = getQuestionsFromQuiz(quiz);
        
        // Xáo trộn câu hỏi nếu cần
        if (shuffleQuestions) {
            Collections.shuffle(questions);
        }

        // Tạo quiz instance questions using existing mappers and entity relationships
        int totalPoints = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            int points = calculateQuestionPoints(question, request);
            totalPoints += points;

            QuizQuestion instanceQuestion = QuizQuestion.builder()
                    .quiz(quiz)
                    .question(question)
                    .isCustom(false)
                    .build();

            instanceQuestion = quizQuestionRepo.save(instanceQuestion);

            Set<Answer> answers = question.getAnswers();
            List<Answer> answerList = new ArrayList<>(answers);
            
            // Xáo trộn đáp án nếu cần
            if (shuffleAnswers) {
                Collections.shuffle(answerList);
            }

            for (int j = 0; j < answerList.size(); j++) {
                Answer answer = answerList.get(j);
                QuizAnswer instanceAnswer = QuizAnswer.builder()
                        .quizQuestion(instanceQuestion)
                        .answer(answer)
                        .isCustom(false)
                        .isCorrect(answer.isAnswerCorrect())
                    .build();

                quizAnswerRepo.save(instanceAnswer);
            }
        }

        instance.setTotalPoints(totalPoints);
        instance = quizInstanceRepo.save(instance);

        return mapQuizInstanceResDTO(instance);
    }

    @Override
    public QuizInstanceResDTO getQuizInstance(Long instanceId, Long userId) {
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance không tồn tại"));

        if (!instance.getUser().getId().equals(userId)) {
            throw new ApiException(AppCode.FORBIDDEN); 
        }

        if (!instance.isInProgress()) {
            throw new ApiException(AppCode.FORBIDDEN);
        }

        return mapQuizInstanceResDTO(instance);
    }

    @Override
    public QuizResultDetailResDTO submitQuiz(QuizSubmissionReqDTO request) {
        QuizInstance instance = quizInstanceRepo.findById(request.getQuizInstanceId())
                .orElseThrow(() -> new NotFoundException("Quiz instance không tồn tại"));

        if (!instance.isInProgress()) {
            throw new ApiException(AppCode.NOT_AVAILABLE,"Quiz instance này không còn trong trạng thái chạy");
        }

        // Xử lý từng câu trả lời
        int earnedPoints = 0;
        for (Map.Entry<Long, String> entry : request.getAnswers().entrySet()) {
            Long questionId = entry.getKey();
            String selectedAnswer = entry.getValue();

            Question question = questionRepo.findById(questionId)
                    .orElseThrow(() -> new NotFoundException("Câu hỏi không tồn tại"));

            // Tìm đáp án đúng
            Answer correctAnswer = question.getAnswers().stream()
                    .filter(Answer::isAnswerCorrect)
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = correctAnswer != null && 
                    correctAnswer.getAnswerName().equals(selectedAnswer);

            // Tìm đáp án user đã chọn
            Long selectedAnswerId = request.getAnswerIds().get(questionId);

            // Tạo user response
            QuizUserResponse response = QuizUserResponse.builder()
                    .quizInstance(instance)
                    .selectedAnswerId(selectedAnswerId)
                    .selectedAnswerText(selectedAnswer)
                    .isCorrect(isCorrect)
                    .pointsEarned(isCorrect ? question.getPoints() : 0)
                    .answeredAt(LocalDateTime.now())
                    .build();

            quizUserResponseRepo.save(response);

            if (isCorrect) {
                earnedPoints += question.getPoints();
            }
        }

        // Cập nhật instance
        instance.setEarnedPoints(earnedPoints);
        instance.submit();
        instance = quizInstanceRepo.save(instance);

        return mapQuizResultDetailResDTO(instance);
    }

    @Override
    public QuizResultDetailResDTO getQuizResult(Long instanceId, Long userId) {
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance không tồn tại"));

        return mapQuizResultDetailResDTO(instance);
    }

    @Override
    public boolean canUserStartQuiz(Long quizId, Long userId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz không tồn tại"));

        if (!quiz.isAvailable()) {
            return false;
        }

        // Kiểm tra số lần tham gia
        long userAttempts = quizInstanceRepo.countByQuizIdAndUserId(quizId, userId);
        return userAttempts < quiz.getMaxAttempts();
    }

    @Override
    public void handleTimedOutInstances() {
        List<QuizInstance> timedOutInstances = quizInstanceRepo.findTimedOutInstances(LocalDateTime.now(), QuizInstanceStatus.IN_PROGRESS);
        
        for (QuizInstance instance : timedOutInstances) {
            instance.markAsTimedOut();
            quizInstanceRepo.save(instance);
            log.info("Quiz instance {} đã hết thời gian", instance.getId());
        }
    }

    @Override
    public void deleteQuizInstance(Long instanceId, Long userId) {
        QuizInstance instance = quizInstanceRepo.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance không tồn tại"));

        if (!instance.getUser().getId().equals(userId)) {
            throw new ApiException(AppCode.NOT_PERMISSION,"Không có quyền xóa quiz instance này");
        }

        quizInstanceRepo.delete(instance);
    }

    @Override
    public PageResDTO<?> getAllQuizInstancesByQuizIdAndLobbyId(Long quizId, Long lobbyId, int page, int size) {
        // Implementation for getting quiz instances by quiz and lobby
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizInstance> instances = quizInstanceRepo.findByQuizIdAndLobbyId(quizId, lobbyId, pageable);
        return convertToPageResDTO.convertPageResponse(instances, pageable, quizInstanceMapper::toQuizInstanceResDTO);
    }

    // Helper methods
    private List<Question> getQuestionsFromQuiz(Quiz quiz) {
        return quiz.getQuestions().stream()
                .map(QuizQuestion::getQuestion)
                .collect(Collectors.toList());
    }

    private int calculateQuestionPoints(Question question, QuizInstanceReqDTO request) {
        // Logic tính điểm có thể được customize theo option
        return question.getPoints() != null ? question.getPoints() : 1;
    }

    private QuizInstanceResDTO mapQuizInstanceResDTO(QuizInstance instance) {
        List<Question> questions = getQuestionsFromQuiz(instance.getQuiz());

        List<QuizInstanceQuestionResDTO> questionDTOs = questions.stream()
                .map(this::mapQuestionResDTO)
                .collect(Collectors.toList());

        QuizInstanceResDTO dto = quizInstanceMapper.toQuizInstanceResDTO(instance);
        dto.setQuestions(questionDTOs);
        dto.setRemainingTimeSeconds(calculateRemainingTime(instance));
        
        return dto;
    }

    private QuizInstanceQuestionResDTO mapQuestionResDTO(Question question) {
        List<Answer> answers = new ArrayList<>(question.getAnswers());

        List<QuizInstanceAnswerResDTO> answerDTOs = answers.stream()
                .map(this::mapAnswerResDTO)
                .collect(Collectors.toList());

        QuizInstanceQuestionResDTO dto = QuizInstanceQuestionResDTO.builder()
                .id(question.getId())
                .questionText(question.getQuestionName())
                .questionType(question.getQuestionType().name())
                .points(question.getPoints())
                .answers(answerDTOs)
                .build();
        
        return dto;
    }

    private QuizResultDetailResDTO mapQuizResultDetailResDTO(QuizInstance instance) {
        List<Question> questions = getQuestionsFromQuiz(instance.getQuiz());

        List<QuestionResultResDTO> questionResults = questions.stream()
                .map(question -> mapQuestionResultResDTO(question, instance))
                .collect(Collectors.toList());

        QuizResultDetailResDTO dto = quizInstanceMapper.toQuizResultDetailResDTO(instance);
        dto.setQuestionResults(questionResults);
        
        return dto;
    }

    private QuestionResultResDTO mapQuestionResultResDTO(Question question, QuizInstance instance) {
        QuizUserResponse userResponse = quizUserResponseRepo.findByQuizInstanceId(instance.getId()).stream()
                .filter(response -> response.getSelectedAnswerText() != null)
                .findFirst()
                .orElse(null);

        List<Answer> allAnswers = new ArrayList<>(question.getAnswers());

        List<AnswerResultResDTO> answerResults = allAnswers.stream()
                .map(answer -> mapAnswerResultResDTO(answer, userResponse))
                .collect(Collectors.toList());

        Answer correctAnswer = question.getAnswers().stream()
                .filter(Answer::isAnswerCorrect)
                .findFirst()
                .orElse(null);

        QuestionResultResDTO dto = QuestionResultResDTO.builder()
                .questionId(question.getId())
                .questionText(question.getQuestionName())
                .questionType(question.getQuestionType().name())
                .points(question.getPoints())
                .earnedPoints(userResponse != null ? userResponse.getPointsEarned() : 0)
                .userAnswer(userResponse != null ? userResponse.getSelectedAnswerText() : null)
                .correctAnswer(correctAnswer != null ? correctAnswer.getAnswerName() : null)
                .correct(userResponse != null && userResponse.isCorrect())
                .skipped(userResponse != null && userResponse.isSkipped())
                .status(userResponse != null ? userResponse.getStatus() : "NOT_ANSWERED")
                .allAnswers(answerResults)
                .build();
        
        return dto;
    }

    private AnswerResultResDTO mapAnswerResultResDTO(Answer answer, QuizUserResponse userResponse) {
        boolean isUserSelected = userResponse != null && 
                answer.getId().equals(userResponse.getSelectedAnswerId());

        return AnswerResultResDTO.builder()
                .answerId(answer.getId())
                .answerText(answer.getAnswerName())
                .correct(answer.isAnswerCorrect())
                .userSelected(isUserSelected)
                .build();
    }

    private long calculateRemainingTime(QuizInstance instance) {
        if (instance.getQuiz().getTimeLimitMinutes() == null) {
            return Long.MAX_VALUE;
        }

        long elapsedSeconds = instance.getElapsedTimeMinutes() * 60;
        long totalSeconds = instance.getQuiz().getTimeLimitMinutes() * 60L;
        return Math.max(0, totalSeconds - elapsedSeconds);
    }
}
