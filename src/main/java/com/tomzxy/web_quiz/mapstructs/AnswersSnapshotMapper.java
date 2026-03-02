package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.responses.AnswersSnapshotResDTO;
import com.tomzxy.web_quiz.models.snapshot.AnswerSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswersSnapshotMapper {
    AnswersSnapshotMapper INSTANCE = Mappers.getMapper(AnswersSnapshotMapper.class);

    @Mapping(target = "correctAnswerText", source = "content")
    @Mapping(target = "questionSnapshotId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "allAnswerOptions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    AnswersSnapshotResDTO toAnswersSnapshotResDTO(AnswerSnapshot answerSnapshot);

    List<AnswersSnapshotResDTO> toAnswersSnapshotResDTOList(List<AnswerSnapshot> answerSnapshots);
}
