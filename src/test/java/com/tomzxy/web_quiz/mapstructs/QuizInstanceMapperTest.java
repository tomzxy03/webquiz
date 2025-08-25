package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.enums.QuizInstanceStatus;
import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.Quiz.QuizInstance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuizInstanceMapperTest {

    private final QuizInstanceMapper mapper = QuizInstanceMapper.INSTANCE;

    @Test
    void testToQuizInstanceResDTO() {
        // Given
        User user = User.builder()
                .userName("testuser")
                .build();
        user.setId(1L);

        Quiz quiz = Quiz.builder()
                .title("Test Quiz")
                .build();
        quiz.setId(1L);

        QuizInstance quizInstance = QuizInstance.builder()
                .quiz(quiz)
                .user(user)
                .startedAt(LocalDateTime.now())
                .timeLimitMinutes(30)
                .shuffleEnabled(true)
                .totalPoints(100)
                .status(QuizInstanceStatus.IN_PROGRESS)
                .build();
        quizInstance.setId(1L);

        // When
        QuizInstanceResDTO result = mapper.toQuizInstanceResDTO(quizInstance);

        // Then
        assertNotNull(result);
        assertEquals(quizInstance.getId(), result.getId());
        assertEquals(quiz.getId(), result.getQuizId());
        assertEquals(quiz.getTitle(), result.getQuizTitle());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(user.getUserName(), result.getUserName());
        assertEquals(quizInstance.getStartedAt(), result.getStartedAt());
        assertEquals(quizInstance.getTimeLimitMinutes(), result.getTimeLimitMinutes());
        assertEquals(quizInstance.isShuffleEnabled(), result.isShuffleEnabled());
        assertEquals(quizInstance.getTotalPoints(), result.getTotalPoints());
        assertEquals(quizInstance.getStatus().name(), result.getStatus());
    }

    @Test
    void testToQuizInstanceResDTOWithNullValues() {
        // When
        QuizInstanceResDTO result = mapper.toQuizInstanceResDTO(null);

        // Then
        assertNull(result);
    }
} 