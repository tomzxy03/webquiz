package com.tomzxy.web_quiz.models.snapshot;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSnapshot {

    // Snapshot id
    private String snapshotId;   // UUID

    // Trace back
    private Long originalAnswerId;

    // Order
    private Integer orderIndex;

    // Content
    private String content;
    private QuestionAndAnswerType type; // TEXT / IMAGE
}


