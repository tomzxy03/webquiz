package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.SubjectReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.SubjectResDTO;

public interface SubjectService {
    PageResDTO<?> get_subjects_pageable(int size , int page);

    SubjectResDTO create_subject(SubjectReqDTO subjectReqDTO);

    SubjectResDTO update_subject(Long subject_id, SubjectReqDTO subjectReqDTO);

    SubjectResDTO get_subject(Long subject_id);

    void delete_subject(Long subject_id);
}
