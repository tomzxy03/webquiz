package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.SubjectReqDTO;
import com.tomzxy.web_quiz.dto.responses.subject.SubjectDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.subject.SubjectResDTO;
import java.util.List;

public interface SubjectService {
    List<SubjectResDTO> get_all_subject();
    List<SubjectDetailResDTO> get_all_subject_and_quiz();

    SubjectResDTO create_subject(SubjectReqDTO subjectReqDTO);

    SubjectResDTO update_subject(Long subject_id, SubjectReqDTO subjectReqDTO);

    SubjectResDTO get_subject(Long subject_id);

    void delete_subject(Long subject_id);
}
