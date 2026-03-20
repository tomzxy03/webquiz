package com.tomzxy.web_quiz.dto.responses.folder;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Folder response")
public class FolderResDTO {
    
    @Schema(description = "Folder ID", example = "1")
    private Long id;
    
    @Schema(description = "Folder name", example = "Unit 1")
    private String name;
    
    @Schema(description = "Parent folder ID (null = root folder)", example = "5")
    private Long parentFolderId;
    
    @Schema(description = "Question Bank ID", example = "1")
    private Long bankId;
    
    @Schema(description = "Number of subfolders")
    private Integer subfolderCount;
    
    @Schema(description = "Number of questions in this folder")
    private Integer questionCount;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    
    @Schema(description = "List of subfolders (optional, nested)", nullable = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<FolderResDTO> children;
}
