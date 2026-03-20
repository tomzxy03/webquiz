package com.tomzxy.web_quiz.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Question creation in folder/bank request")
public class QuestionFolderReqDTO {
    
    @Schema(description = "Folder ID (optional - null = root folder)", example = "5")
    private Long folderId;
    
    private QuestionReqDTO questions;
}
