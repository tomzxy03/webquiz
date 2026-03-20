package com.tomzxy.web_quiz.mapstructs.Folder;

import com.tomzxy.web_quiz.dto.requests.FolderReqDTO;
import com.tomzxy.web_quiz.dto.responses.folder.FolderResDTO;
import com.tomzxy.web_quiz.models.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FolderMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "parentFolderId", source = "parent.id")
    @Mapping(target = "bankId", source = "bank.id")
    @Mapping(target = "subfolderCount", expression = "java(folder.getChildren() != null ? folder.getChildren().size() : 0)")
    @Mapping(target = "questionCount", expression = "java(folder.getQuestions() != null ? folder.getQuestions().size() : 0)")
    @Mapping(target = "children", ignore = true)
    FolderResDTO toFolderResDTO(Folder folder);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Folder toFolder(FolderReqDTO folderReqDTO);
}
