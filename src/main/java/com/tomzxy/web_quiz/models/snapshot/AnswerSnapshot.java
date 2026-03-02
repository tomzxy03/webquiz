package com.tomzxy.web_quiz.models.snapshot;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import com.tomzxy.web_quiz.models.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSnapshot extends BaseEntity {
    private String snapshotId;   // UUID
    private Long originalAnswerId;
    private Integer orderIndex;
    private String content;
    @Enumerated(EnumType.STRING)
    private QuestionAndAnswerType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_snapshot_id")
    private QuestionSnapshot questionSnapshot;
}


