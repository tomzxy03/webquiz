package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface QuizService {
    DataResDTO<PageResDTO<List<QuizResDTO>>> getAll(Pageable pageable);
    DataResDTO<QuizResDTO> getById(Long id);
    DataResDTO<QuizResDTO> create(QuizReqDTO dto);
    DataResDTO<QuizResDTO> update(Long id, QuizReqDTO dto);
    void delete(Long id);
} 