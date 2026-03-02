package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.responses.subject.SubjectDetailResDTO;
import com.tomzxy.web_quiz.models.Subject;
import com.tomzxy.web_quiz.dto.requests.SubjectReqDTO;
import com.tomzxy.web_quiz.dto.responses.subject.SubjectResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.SubjectMapper;
import com.tomzxy.web_quiz.repositories.SubjectRepo;
import com.tomzxy.web_quiz.services.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepo subjectRepo;
    private final SubjectMapper subjectMapper;

    @Override
    public List<SubjectResDTO> get_all_subject() {
        List<Subject> subjects = subjectRepo.findAllByActive(true).orElseThrow(() -> new NotFoundException("Subject not found!"));
        return subjects.stream().map(subjectMapper::toSubjectResDTO).collect(Collectors.toList());
    }

    @Override
    public List<SubjectDetailResDTO> get_all_subject_and_quiz(){
        return subjectRepo.getAllSubjectsAndCountOfQuizzes(true).orElseThrow(() -> new NotFoundException("Subject not found"));
    }

    @Override
    public SubjectResDTO create_subject(SubjectReqDTO subjectReqDTO) {
        Subject subject = subjectMapper.toSubject(subjectReqDTO);
        return subjectMapper.toSubjectResDTO(subjectRepo.save(subject));
    }

    @Override
    public SubjectResDTO update_subject(Long subject_id, SubjectReqDTO subjectReqDTO) {
        Subject subject = findSubjectById(subject_id);
        subjectMapper.update_subject(subject,subjectReqDTO);

        return subjectMapper.toSubjectResDTO(subjectRepo.save(subject));
    }

    @Override      
    public void delete_subject(Long subject_id) {
        Subject subject = findSubjectById(subject_id);
        subject.setActive(false);
        try{
            subjectRepo.save(subject);
        }catch (Exception e){
            throw new RuntimeException("Cannot delete subject: ",e);
        }
    }

    private Subject findSubjectById(Long id){
        return subjectRepo.findById(id).orElseThrow(()-> new NotFoundException("Subject not found"));
    }

    @Override
    public SubjectResDTO get_subject(Long subject_id) {
        return subjectMapper.toSubjectResDTO(findSubjectById(subject_id));
    }

}
