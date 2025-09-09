package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.responses.AnswersSnapshotResDTO;
import com.tomzxy.web_quiz.models.snapshot.AnswersSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswersSnapshotMapper {
    AnswersSnapshotMapper INSTANCE = Mappers.getMapper(AnswersSnapshotMapper.class);

    @Mapping(target = "questionSnapshotId", source = "questionSnapshot.id")
    AnswersSnapshotResDTO toAnswersSnapshotResDTO(AnswersSnapshot answersSnapshot);

    List<AnswersSnapshotResDTO> toAnswersSnapshotResDTOList(List<AnswersSnapshot> answersSnapshots);
}
