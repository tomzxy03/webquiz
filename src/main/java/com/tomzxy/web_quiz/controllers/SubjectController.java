package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.requests.SubjectReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;
import com.tomzxy.web_quiz.dto.responses.SubjectResDTO;
import com.tomzxy.web_quiz.services.SubjectService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Subject.BASE)

public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping()
    public DataResDTO<List<SubjectResDTO>> getAllSubject(){
        log.info("Get all Subjects");
        return DataResDTO.ok(subjectService.get_all_subject());
    }

    @GetMapping(ApiDefined.Subject.ID)
    public DataResDTO<SubjectResDTO> getSubject(@PathVariable Long subjectId){
        log.info("get Subject by {}", subjectId);
        return DataResDTO.ok(subjectService.get_subject(subjectId));
    }
    @PostMapping()
    public DataResDTO<Void> addSubject(@Valid @RequestBody SubjectReqDTO subjectReqDTO){
        log.info("add Subject");
        subjectService.create_subject(subjectReqDTO);
        return DataResDTO.create();
    }

    @PutMapping(ApiDefined.Subject.ID)
    public DataResDTO<SubjectResDTO> updateSubject(@PathVariable Long subjectId, @RequestBody @Valid SubjectReqDTO subjectReqDTO){
        log.info("Update Subject with id {}", subjectId);
        return DataResDTO.update(subjectService.update_subject(subjectId,subjectReqDTO));
    }

    @DeleteMapping(ApiDefined.Subject.ID)
    public DataResDTO<Void> deleteSubject(@PathVariable Long subjectId){
        log.info("Delete Subject with id {}", subjectId);
        subjectService.delete_subject(subjectId);
        return DataResDTO.delete();
    }
}
