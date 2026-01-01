package com.tomzxy.web_quiz.mapstructs.Folder;


import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component(value = "spring")
public class FolderMapper {
    FolderMapper MAPPER = Mappers.getMapper(FolderMapper.class);


}
