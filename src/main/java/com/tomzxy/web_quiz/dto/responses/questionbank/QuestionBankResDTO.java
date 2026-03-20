package com.tomzxy.web_quiz.dto.responses.questionbank;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionBankResDTO {
    Long id;
    String uuid;
    Long ownerId;
    String ownerName;
    Integer questionCount;
    Integer folderCount;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
