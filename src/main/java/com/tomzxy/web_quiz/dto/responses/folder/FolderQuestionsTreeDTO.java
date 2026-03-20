package com.tomzxy.web_quiz.dto.responses.folder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Folder with questions tree structure")
public class FolderQuestionsTreeDTO {
    
    @Schema(description = "Folder ID")
    private Long folderId;
    
    @Schema(description = "Folder name")
    private String folderName;
    
    @Schema(description = "Questions in this folder")
    private List<?> questions;
    
    @Schema(description = "Subfolders")
    private List<FolderQuestionsTreeDTO> subfolders;
}
