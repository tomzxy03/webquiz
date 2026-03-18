package com.tomzxy.web_quiz.models.snapshot;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
public class AnswerSnapshot {
    private String snapshotId;   // UUID
    private Long originalAnswerId;
    private Integer orderIndex;
    private String content;
    @Enumerated(EnumType.STRING)
    private QuestionAndAnswerType type;

}


