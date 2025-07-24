package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.requests.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.QuizMapper;
import com.tomzxy.web_quiz.models.Quiz;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private QuizMapper quizMapper;

    @Override
    public DataResDTO<PageResDTO<java.util.List<QuizResDTO>>> getAll(Pageable pageable) {
        Page<Quiz> page = quizRepo.findAll(pageable);
        PageResDTO<java.util.List<QuizResDTO>> pageResDTO = PageResDTO.<java.util.List<QuizResDTO>>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .total_page(page.getTotalPages())
                .items(page.getContent().stream().map(quizMapper::toDto).collect(Collectors.toList()))
                .build();
        return DataResDTO.ok(pageResDTO);
    }

    @Override
    public DataResDTO<QuizResDTO> getById(Long id) {
        Quiz quiz = quizRepo.findById(id).orElseThrow(() -> new NotFoundException("Quiz not found"));
        return DataResDTO.ok(quizMapper.toDto(quiz));
    }

    @Override
    @Transactional
    public DataResDTO<QuizResDTO> create(QuizReqDTO dto) {
        Quiz quiz = quizMapper.toEntity(dto);
        quiz = quizRepo.save(quiz);
        return DataResDTO.create(quizMapper.toDto(quiz));
    }

    @Override
    public DataResDTO<QuizResDTO> update(Long id, QuizReqDTO dto) {
        Quiz quiz = quizRepo.findById(id).orElseThrow(() -> new NotFoundException("Quiz not found"));
        Quiz updatedQuiz = quizMapper.toEntity(dto);
        updatedQuiz.setId(quiz.getId());
        updatedQuiz = quizRepo.save(updatedQuiz);
        return DataResDTO.update(quizMapper.toDto(updatedQuiz));
    }

    @Override
    public void delete(Long id) {
        Quiz quiz = quizRepo.findById(id).orElseThrow(() -> new NotFoundException("Quiz not found"));
        quizRepo.delete(quiz);
    }
} 