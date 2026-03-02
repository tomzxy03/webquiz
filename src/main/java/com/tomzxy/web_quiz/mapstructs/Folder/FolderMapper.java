package com.tomzxy.web_quiz.mapstructs.Folder;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface FolderMapper {
    FolderMapper MAPPER = Mappers.getMapper(FolderMapper.class);
}
