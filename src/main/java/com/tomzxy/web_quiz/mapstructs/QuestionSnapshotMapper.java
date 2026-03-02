package com.tomzxy.web_quiz.mapstructs;

import com.tomzxy.web_quiz.dto.responses.question.QuestionSnapshotResDTO;
import com.tomzxy.web_quiz.models.snapshot.QuestionSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AnswersSnapshotMapper.class})
public interface QuestionSnapshotMapper {
    QuestionSnapshotMapper INSTANCE = Mappers.getMapper(QuestionSnapshotMapper.class);

    @Mapping(target = "answerSnapshots", source = "answers")
    @Mapping(target = "questionText", source = "content")
    @Mapping(target = "questionType", source = "type")
    @Mapping(target = "questionPoints", source = "points")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    QuestionSnapshotResDTO toQuestionSnapshotResDTO(QuestionSnapshot questionSnapshot);

    List<QuestionSnapshotResDTO> toQuestionSnapshotResDTOList(List<QuestionSnapshot> questionSnapshots);
}
