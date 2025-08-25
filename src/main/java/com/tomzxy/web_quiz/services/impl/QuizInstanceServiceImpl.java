package com.tomzxy.web_quiz.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizInstanceReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizSubmissionReqDTO;
import com.tomzxy.web_quiz.dto.responses.*;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizInstance;
import com.tomzxy.web_quiz.repositories.*;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import com.tomzxy.web_quiz.mapstructs.QuizInstanceMapper;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
import com.tomzxy.web_quiz.mapstructs.AnswerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final QuizInstanceQuestionRepo quizInstanceQuestionRepo;
    private final QuizInstanceAnswerRepo quizInstanceAnswerRepo;
    private final QuizUserResponseRepo quizUserResponseRepo;
    private final QuizRepo quizRepo;
    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;
    private final QuizInstanceMapper quizInstanceMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;

    @Override
    public QuizInstanceResDTO createQuizInstance(QuizInstanceReqDTO request) {

        Quiz quiz = quizRepo.findById(request.getQuizId())
                .orElseThrow(() -> new NotFoundException("Quiz không tồn tại"));
        
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));

        // kiểm tra user da qua so lan tham gia quiz
        if (!canUserStartQuiz(request.getQuizId(), request.getUserId())) {
            throw new RuntimeException("User không có quyền tham gia quiz này");
        }

        QuizInstance instance = QuizInstance.builder()
                .quiz(quiz)
                .user(user)
                .startedAt(LocalDateTime.now())
                .shuffleEnabled(request.isShuffleQuestions())
                .timeLimitMinutes(request.getTimeLimitMinutes())
                .status(QuizInstanceStatus.IN_PROGRESS)
                .build();

        instance = quizInstanceRepo.save(instance);

        List<Question> questions = getQuestionsFromQuiz(quiz);
        
        // Xáo trộn câu hỏi nếu cần
        if (request.isShuffleQuestions()) {
            Collections.shuffle(questions);
        }

        // Tạo quiz instance questions using existing mappers and entity relationships
        int totalPoints = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            int points = calculateQuestionPoints(question, request);
            totalPoints += points;

            QuizInstanceQuestion instanceQuestion = QuizInstanceQuestion.builder()
                    .quizInstance(instance)
                    .originalQuestion(question) // Leverage entity relationship
                    .displayOrder(i + 1)
                    .points(points)
                    .questionText(question.getQuestionName())
                    .questionType(question.getQuestionType().name())
                    .build();

            instanceQuestion = quizInstanceQuestionRepo.save(instanceQuestion);

            Set<Answer> answers = question.getAnswers();
            List<Answer> answerList = new ArrayList<>(answers);
            
            // Xáo trộn đáp án nếu cần
            if (request.isShuffleAnswers()) {
                Collections.shuffle(answerList);
            }

            for (int j = 0; j < answerList.size(); j++) {
                Answer answer = answerList.get(j);
                QuizInstanceAnswer instanceAnswer = QuizInstanceAnswer.builder()
                        .quizInstanceQuestion(instanceQuestion)
                        .originalAnswer(answer) // Leverage entity relationship
                        .displayOrder(j + 1)
                        .answerText(answer.getAnswerName())
                        .isCorrect(answer.isAnswerCorrect())
                        .optionLabel(generateOptionLabel(j))
                        .build();

                quizInstanceAnswerRepo.save(instanceAnswer);
            }
        }


        instance.setTotalPoints(totalPoints);
        instance = quizInstanceRepo.save(instance);


        saveOrderInformation(instance, questions, request);

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
            Long questionInstanceId = entry.getKey();
            String selectedAnswer = entry.getValue();

            QuizInstanceQuestion question = quizInstanceQuestionRepo.findById(questionInstanceId)
                    .orElseThrow(() -> new NotFoundException("Câu hỏi không tồn tại"));

            // Tìm đáp án đúng
            QuizInstanceAnswer correctAnswer = quizInstanceAnswerRepo
                    .findByQuizInstanceQuestionIdAndIsCorrectTrue(questionInstanceId)
                    .stream()
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = correctAnswer != null && 
                    correctAnswer.getAnswerText().equals(selectedAnswer);

            // Tìm đáp án user đã chọn
            Long selectedAnswerId = request.getAnswerIds().get(questionInstanceId);

            // Tạo user response
            QuizUserResponse response = QuizUserResponse.builder()
                    .quizInstance(instance)
                    .quizInstanceQuestion(question)
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

    /**
     * Example method showing how to leverage existing mappers for creating quiz instances
     * This demonstrates collaboration with existing mappers and entity relationships
     */
    private void createQuizInstanceFromOriginalData(QuizInstance instance, List<Question> questions, QuizInstanceReqDTO request) {
        // Example of using existing mappers to get original question data
        for (int i = 0; i < questions.size(); i++) {
            Question originalQuestion = questions.get(i);
            
            // Use existing QuestionMapper to get question details if needed
            // QuestionResDTO questionDetails = questionMapper.toQuestionResDTO(originalQuestion);
            
            int points = calculateQuestionPoints(originalQuestion, request);

            QuizInstanceQuestion instanceQuestion = QuizInstanceQuestion.builder()
                    .quizInstance(instance)
                    .originalQuestion(originalQuestion) // Leverage the entity relationship
                    .displayOrder(i + 1)
                    .points(points)
                    .questionText(originalQuestion.getQuestionName()) // Use original data
                    .questionType(originalQuestion.getQuestionType().name())
                    .build();

            instanceQuestion = quizInstanceQuestionRepo.save(instanceQuestion);

            // Use existing AnswerMapper for original answers
            Set<Answer> originalAnswers = originalQuestion.getAnswers();
            List<Answer> answerList = new ArrayList<>(originalAnswers);
            
            if (request.isShuffleAnswers()) {
                Collections.shuffle(answerList);
            }

            for (int j = 0; j < answerList.size(); j++) {
                Answer originalAnswer = answerList.get(j);
                
                // Use existing AnswerMapper to get answer details if needed
                // AnswerResDTO answerDetails = answerMapper.toAnswerResDTO(originalAnswer);
                
                QuizInstanceAnswer instanceAnswer = QuizInstanceAnswer.builder()
                        .quizInstanceQuestion(instanceQuestion)
                        .originalAnswer(originalAnswer) // Leverage the entity relationship
                        .displayOrder(j + 1)
                        .answerText(originalAnswer.getAnswerName()) // Use original data
                        .isCorrect(originalAnswer.isAnswerCorrect())
                        .optionLabel(generateOptionLabel(j))
                        .build();

                quizInstanceAnswerRepo.save(instanceAnswer);
            }
        }
    }

    private String generateOptionLabel(int index) {
        return String.valueOf((char) ('A' + index));
    }

    private void saveOrderInformation(QuizInstance instance, List<Question> questions, QuizInstanceReqDTO request) {
        try {
            // Lưu thứ tự câu hỏi
            List<Long> questionOrder = questions.stream()
                    .map(Question::getId)
                    .collect(Collectors.toList());
            instance.setQuestionOrder(objectMapper.writeValueAsString(questionOrder));

            // Lưu thứ tự đáp án (có thể mở rộng sau)
            instance.setAnswerOrder("{}");
            
            quizInstanceRepo.save(instance);
        } catch (JsonProcessingException e) {
            log.error("Lỗi khi lưu thông tin thứ tự", e);
        }
    }

    private QuizInstanceResDTO mapQuizInstanceResDTO(QuizInstance instance) {
        List<QuizInstanceQuestion> questions = quizInstanceQuestionRepo
                .findByQuizInstanceIdOrderByDisplayOrder(instance.getId());

        List<QuizInstanceQuestionResDTO> questionDTOs = questions.stream()
                .map(this::mapQuestionResDTO)
                .collect(Collectors.toList());

        QuizInstanceResDTO dto = quizInstanceMapper.toQuizInstanceResDTO(instance);
        dto.setQuestions(questionDTOs);
        dto.setRemainingTimeSeconds(calculateRemainingTime(instance));
        
        return dto;
    }

    private QuizInstanceQuestionResDTO mapQuestionResDTO(QuizInstanceQuestion question) {
        List<QuizInstanceAnswer> answers = quizInstanceAnswerRepo
                .findByQuizInstanceQuestionIdOrderByDisplayOrder(question.getId());

        List<QuizInstanceAnswerResDTO> answerDTOs = quizInstanceMapper.toQuizInstanceAnswerResDTOList(answers);

        QuizInstanceQuestionResDTO dto = quizInstanceMapper.toQuizInstanceQuestionResDTO(question);
        dto.setAnswers(answerDTOs);
        
        // Leverage the original question relationship if needed
        if (question.getOriginalQuestion() != null) {
            // We could use questionMapper here if we needed to map the original question
            // For now, we're using the snapshot data stored in the instance
        }
        
        return dto;
    }

    private QuizInstanceAnswerResDTO mapAnswerResDTO(QuizInstanceAnswer answer) {
        return quizInstanceMapper.toQuizInstanceAnswerResDTO(answer);
    }

    private QuizResultDetailResDTO mapQuizResultDetailResDTO(QuizInstance instance) {
        List<QuizInstanceQuestion> questions = quizInstanceQuestionRepo
                .findByQuizInstanceIdOrderByDisplayOrder(instance.getId());

        List<QuestionResultResDTO> questionResults = questions.stream()
                .map(this::mapQuestionResultResDTO)
                .collect(Collectors.toList());

        QuizResultDetailResDTO dto = quizInstanceMapper.toQuizResultDetailResDTO(instance);
        dto.setQuestionResults(questionResults);
        
        return dto;
    }

    private QuestionResultResDTO mapQuestionResultResDTO(QuizInstanceQuestion question) {
        QuizUserResponse userResponse = quizUserResponseRepo
                .findByQuizInstanceIdAndQuizInstanceQuestionId(question.getQuizInstance().getId(), question.getId())
                .orElse(null);

        List<QuizInstanceAnswer> allAnswers = quizInstanceAnswerRepo
                .findByQuizInstanceQuestionIdOrderByDisplayOrder(question.getId());

        List<AnswerResultResDTO> answerResults = allAnswers.stream()
                .map(answer -> mapAnswerResultResDTO(answer, userResponse))
                .collect(Collectors.toList());

        QuizInstanceAnswer correctAnswer = question.getCorrectAnswer();

        QuestionResultResDTO dto = quizInstanceMapper.toQuestionResultResDTO(question);
        dto.setEarnedPoints(userResponse != null ? userResponse.getPointsEarned() : 0);
        dto.setUserAnswer(userResponse != null ? userResponse.getSelectedAnswerText() : null);
        dto.setCorrectAnswer(correctAnswer != null ? correctAnswer.getAnswerText() : null);
        dto.setCorrect(userResponse != null && userResponse.isCorrect());
        dto.setSkipped(userResponse != null && userResponse.isSkipped());
        dto.setStatus(userResponse != null ? userResponse.getStatus() : "NOT_ANSWERED");
        dto.setAllAnswers(answerResults);
        
        return dto;
    }

    private AnswerResultResDTO mapAnswerResultResDTO(QuizInstanceAnswer answer, QuizUserResponse userResponse) {
        boolean isUserSelected = userResponse != null && 
                answer.getId().equals(userResponse.getSelectedAnswerId());

        AnswerResultResDTO dto = quizInstanceMapper.toAnswerResultResDTO(answer);
        dto.setUserSelected(isUserSelected);
        
        return dto;
    }

    private long calculateRemainingTime(QuizInstance instance) {
        if (instance.getTimeLimitMinutes() == null) {
            return Long.MAX_VALUE;
        }

        long elapsedSeconds = instance.getElapsedTimeMinutes() * 60;
        long totalSeconds = instance.getTimeLimitMinutes() * 60L;
        return Math.max(0, totalSeconds - elapsedSeconds);
    }
} 