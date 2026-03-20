package com.tomzxy.web_quiz.dto.responses.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Question response with folder info")
public class QuestionFolderResDTO {
    
    @Schema(description = "Question ID")
    private Long id;
    
    private QuestionResDTO question;
    
    @Schema(description = "Question Bank ID")
    private Long bankId;
    
    @Schema(description = "Folder ID (null = root level)")
    private Long folderId;
    
    @Schema(description = "Folder name (null = root level)")
    private String folderName;

    
    @Schema(description = "Number of answers")
    private Integer answerCount;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
