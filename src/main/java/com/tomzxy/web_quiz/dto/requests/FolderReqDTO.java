package com.tomzxy.web_quiz.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Folder creation/update request")
public class FolderReqDTO {
    
    @Schema(description = "Folder name", example = "Unit 1")
    private String name;
    
    @Schema(description = "Parent folder ID (null = root folder)", example = "5")
    private Long parentFolderId;
}
