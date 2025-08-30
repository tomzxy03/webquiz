package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceQuestionResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceAnswerResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResultResDTO;
import com.tomzxy.web_quiz.dto.responses.AnswerResultResDTO;
import com.tomzxy.web_quiz.models.QuizUserResponse;
import com.tomzxy.web_quiz.models.Quiz.QuizInstance;
import com.tomzxy.web_quiz.models.snapshot.QuizInstanceAnswer;
import com.tomzxy.web_quiz.models.snapshot.QuizInstanceQuestion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {QuizMapper.class, QuestionMapper.class, AnswerMapper.class})
public interface QuizInstanceMapper {
    QuizInstanceMapper INSTANCE = Mappers.getMapper(QuizInstanceMapper.class);

    // QuizInstance to QuizInstanceResDTO
    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "quizTitle", source = "quiz.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "questions", ignore = true) // Will be handled manually in service
    @Mapping(target = "remainingTimeSeconds", ignore = true) // Will be calculated in service
    QuizInstanceResDTO toQuizInstanceResDTO(QuizInstance quizInstance);

    // QuizInstanceQuestion to QuizInstanceQuestionResDTO
    @Mapping(target = "answers", ignore = true) // Will be handled manually in service
    QuizInstanceQuestionResDTO toQuizInstanceQuestionResDTO(QuizInstanceQuestion question);

    // QuizInstanceAnswer to QuizInstanceAnswerResDTO
    QuizInstanceAnswerResDTO toQuizInstanceAnswerResDTO(QuizInstanceAnswer answer);

    // QuizInstance to QuizResultDetailResDTO
    @Mapping(target = "quizInstanceId", source = "id")
    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "quizTitle", source = "quiz.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "scorePercentage", source = "scorePercentage")
    @Mapping(target = "totalTimeSpentMinutes", source = "elapsedTimeMinutes")
    @Mapping(target = "questionResults", ignore = true) // Will be handled manually in service
    QuizResultDetailResDTO toQuizResultDetailResDTO(QuizInstance quizInstance);

    // QuizInstanceQuestion to QuestionResultResDTO
    @Mapping(target = "questionInstanceId", source = "id")
    @Mapping(target = "userAnswer", ignore = true) // Will be handled manually in service
    @Mapping(target = "correctAnswer", ignore = true) // Will be handled manually in service
    @Mapping(target = "isCorrect", ignore = true) // Will be handled manually in service
    @Mapping(target = "isSkipped", ignore = true) // Will be handled manually in service
    @Mapping(target = "status", ignore = true) // Will be handled manually in service
    @Mapping(target = "earnedPoints", ignore = true) // Will be handled manually in service
    @Mapping(target = "allAnswers", ignore = true) // Will be handled manually in service
    QuestionResultResDTO toQuestionResultResDTO(QuizInstanceQuestion question);

    // QuizInstanceAnswer to AnswerResultResDTO
    @Mapping(target = "answerInstanceId", source = "id")
    @Mapping(target = "isUserSelected", ignore = true) // Will be handled manually in service
    AnswerResultResDTO toAnswerResultResDTO(QuizInstanceAnswer answer);

    // List mappings
    List<QuizInstanceQuestionResDTO> toQuizInstanceQuestionResDTOList(List<QuizInstanceQuestion> questions);
    List<QuizInstanceAnswerResDTO> toQuizInstanceAnswerResDTOList(List<QuizInstanceAnswer> answers);
    List<QuestionResultResDTO> toQuestionResultResDTOList(List<QuizInstanceQuestion> questions);
    List<AnswerResultResDTO> toAnswerResultResDTOList(List<QuizInstanceAnswer> answers);



    // Helper method to convert status enum to string
    @Named("statusToString")
    default String statusToString(com.tomzxy.web_quiz.enums.QuizInstanceStatus status) {
        return status != null ? status.name() : null;
    }

    // Helper method to get user answer from user response
    @Named("getUserAnswer")
    default String getUserAnswer(QuizInstanceQuestion question, QuizUserResponse userResponse) {
        return userResponse != null ? userResponse.getSelectedAnswerText() : null;
    }

    // Helper method to get correct answer text
    @Named("getCorrectAnswer")
    default String getCorrectAnswer(QuizInstanceQuestion question) {
        QuizInstanceAnswer correctAnswer = question.getCorrectAnswer();
        return correctAnswer != null ? correctAnswer.getAnswerText() : null;
    }

    // Helper method to check if answer is selected by user
    @Named("isUserSelected")
    default boolean isUserSelected(QuizInstanceAnswer answer, QuizUserResponse userResponse) {
        return userResponse != null && answer.getId().equals(userResponse.getSelectedAnswerId());
    }

    // Helper method to convert question type enum to string
    @Named("questionTypeToString")
    default String questionTypeToString(com.tomzxy.web_quiz.enums.QuestionAndAnswerType questionType) {
        return questionType != null ? questionType.name() : null;
    }
} 