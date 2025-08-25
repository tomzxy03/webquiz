package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.requests.SubjectReqDTO;
import com.tomzxy.web_quiz.dto.responses.SubjectResDTO;
import com.tomzxy.web_quiz.models.Subject;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = Subject.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubjectMapper {
    SubjectMapper MAPPER = Mappers.getMapper(SubjectMapper.class);

    Subject toSubject(SubjectReqDTO subjectReqDTO);

    SubjectResDTO toSubjectResDTO(Subject subject);

    @Mapping(target = "quizzes", ignore = true)
    void update_subject(@MappingTarget Subject subject, SubjectReqDTO subjectReqDTO);

    List<SubjectResDTO> toListSubjectResDTO(List<Subject> subjects);
}