package com.tomzxy.web_quiz.mapstructs.Folder;

import com.tomzxy.web_quiz.dto.responses.questionbank.QuestionBankResDTO;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionBankMapper {
    
    @Mapping(target = "id", source = "owner.id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.userName")
    @Mapping(target = "questionCount", expression = "java(questionBank.getQuestions() != null ? questionBank.getQuestions().size() : 0)")
    @Mapping(target = "folderCount", expression = "java(questionBank.getFolders() != null ? questionBank.getFolders().size() : 0)")
    QuestionBankResDTO toQuestionBankResDTO(QuestionBank questionBank);
}
