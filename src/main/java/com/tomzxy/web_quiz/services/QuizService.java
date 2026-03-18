package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.filter.QuizFilterReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizQuestionReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import org.springframework.data.domain.Page;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;

import java.util.List;

public interface QuizService {
    PageResDTO<?> getAll(int page, int size);

    PageResDTO<?> getAllWithFilter(QuizFilterReqDTO filter, int page, int size);

    PageResDTO<?> getLatestQuizzes(int page, int size);

    PageResDTO<?> getPopularQuizzes(int page, int size);

    QuizResDTO getById(Long id);

    QuizDetailResDTO getQuizDetail(Long id);

    QuizInstanceResDTO getActiveInstance(Long quizId);

    QuizResDTO create(QuizReqDTO dto);
    void create_Questions(Long quizId, List<QuizQuestionReqDTO> questionReqDTO);

    QuizResDTO update(Long id, QuizReqDTO dto);
    void update_Questions(Long quizId, List<QuizQuestionReqDTO> questionReqDTO);

    void delete(Long id);
}