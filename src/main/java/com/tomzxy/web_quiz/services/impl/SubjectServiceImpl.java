package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.UserMapper;
import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.services.SubjectService;
import com.tomzxy.web_quiz.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepo subjectRepo;
    private final SubjectMapper subjectMapper;
    private final ConvertToPageResDTO convertToPageResDTO;

    @Override
    public PageResDTO<?> get_subjects_pageable(int size, int page) {
        Pageable pageable = PageRequest.of(size,page);
        Page<Subject> subjects = subjectRepo.findAllByActive(true, pageable);

        return convertToPageResDTO.convertToPageResponse(subjects,page,size,SubjectResDTO.class);
    }

    @Override
    public SubjectResDTO create_subject(SubjectReqDTO subjectReqDTO) {
        Subject subject = subjectMapper.toSubject(subjectReqDTO);
        return subjectMapper.toSubjectResDTO(subjectRepo.save(subject));
    }

    @Override
    public SubjectResDTO update_subject(Long subject_id, SubjectReqDTO subjectReqDTO) {
        Subject subject = findSubjectById(subject_id);
        subjectMapper.updateSubject(subject,subjectReqDTO);

        return subjectMapper.toSubjectResDTO(subjectRepo.save(subject));
    }

    @Override
    public SubjectResDTO get_subject(Long subject_id) {
        return subjectMapper.toSubjectResDTO(findSubjectById(subject_id));
    }

    @Override
    public void delete_subject(Long subject_id) {
        Subject subject = findSubjectById(subject_id);
        subject.set_active(false);
        try{
            subjectRepo.save(subject);
        }catch (Exception e){
            throw new RuntimeException("Cannot delete subject: ",e);
        }
    }

    private Subject findSubjectById(Long id){
        return subjectRepo.findById(id).orElseThrow(()-> new NotFoundException("Subject not found"));
    }


}
