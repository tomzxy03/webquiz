package com.tomzxy.web_quiz.dto.responses.question;

import com.tomzxy.web_quiz.dto.responses.AnswersSnapshotResDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSnapshotResDTO {
    
    private Long id;
    private String questionText;
    private String questionType;
    private Long questionPoints;
    private List<AnswersSnapshotResDTO> answerSnapshots;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
