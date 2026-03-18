package com.tomzxy.web_quiz.services.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.ExistedException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.AnswerMapper;
import com.tomzxy.web_quiz.mapstructs.QuestionMapper;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.repositories.QuestionRepo;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.DigestUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepo questionRepo;
    private final ConvertToPageResDTO convertToPageResDTO;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final QuizRepo quizRepo;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    @Override
    public PageResDTO<?> get_Questions_pageable(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Question> questions = questionRepo.findAll(pageable);
        // config to use mapstruct
        return convertToPageResDTO.convertPageResponse(questions,pageable,questionMapper::toQuestionResDTO);
    }

    @Override
    @Transactional
    public void create_Question(QuestionReqDTO questionReqDTO) {
        boolean flag = questionRepo.existsByQuestionName(questionReqDTO.getQuestionName());// check if the question name is already existed
        log.info("create question and answer {} {}", questionReqDTO.getQuestionName(), questionReqDTO.getAnswers());

        if(flag){
            throw new ExistedException(AppCode.DATA_EXISTED, "Question has been existed");
        }
        Question question = questionMapper.toQuestion(questionReqDTO); // convert the question request dto to question

        Set<Answer> answers = answerMapper.toSetAnswer(questionReqDTO.getAnswers());// convert the answer request dto to answer
        answers.forEach(Answer::activate);
        question.addAnswers(answers); // save the answers to the question
        question.setContentHash(generateContentHash(question));
        question.activate();
        questionRepo.save(question); // save the question to the database
    }

    @Override
    @Transactional
    public void create_Questions(Long quizId, List<QuestionReqDTO> dtos) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow(()-> new NotFoundException("Quiz not found"));
        List<Question> questions = questionMapper.toListQuestion(dtos);

        // Generate content hash
        for (Question q : questions) {
            String hash = generateContentHash(q);
            q.setContentHash(hash);
        }

        //Collect hashes
        List<String> incomingHashes = questions.stream()
                .map(Question::getContentHash)
                .toList();

        //Check duplicate in DB
        List<String> existedHashes = questionRepo.findAllByContentHashIn(incomingHashes);

        if (!existedHashes.isEmpty()) {
            throw new ExistedException(
                    AppCode.DATA_EXISTED,
                    "Question content duplicated"
            );
        }
        questionRepo.saveAll(questions);
    }

    @Override
    public QuestionResDTO update_Question(Long question_id, QuestionReqDTO questionReqDTO) {
        Question question = findQuestion(question_id);

        questionMapper.updateQuestion(question,questionReqDTO); // update the question
        String newHash = generateContentHash(question);
        if(!newHash.equals(question.getContentHash())){
            question.setContentHash(newHash);
        }
        Set<Answer> answers = answerMapper.toSetAnswer(questionReqDTO.getAnswers()); // convert the answer request dto to answer
        question.addAnswers(answers); // save the answers to the question
        // save the question to the database
        return questionMapper.toQuestionResDTO(questionRepo.save(question));
    }

    @Override
    public QuestionResDTO get_Question(Long question_id) {
        Question question = findQuestion(question_id);
        return questionMapper.toQuestionResDTO(question);
    }

    @Override
    public void delete_Question(Long question_id) {

    }
    public Question findQuestion(Long question_id){
        return questionRepo.findById(question_id).orElseThrow(()->new ApiException(AppCode.NOT_AVAILABLE, "Question not found!"));
    }


    public String generateContentHash(Question question) {
        try {

            List<Map<String, Object>> sortedAnswers =
                    question.getAnswers()
                            .stream()
                            .sorted(Comparator.comparing(a -> normalize(a.getAnswerName())))
                            .map(a -> {
                                Map<String, Object> map = new TreeMap<>();
                                map.put("content", normalize(a.getAnswerName()));
                                map.put("correct", a.isAnswerCorrect());
                                return map;
                            })
                            .toList();

            Map<String, Object> payload = new TreeMap<>();
            payload.put("questionName", normalize(question.getQuestionName()));
            payload.put("questionType", question.getQuestionType().name());
            payload.put("answers", sortedAnswers);

            String json = OBJECT_MAPPER.writeValueAsString(payload);

            return DigestUtils.sha256Hex(json);

        } catch (Exception e) {
            throw new RuntimeException("Failed to hash content of question", e);
        }
    }
    public String normalize(String input){
        if(input == null) return "";
        return input.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
