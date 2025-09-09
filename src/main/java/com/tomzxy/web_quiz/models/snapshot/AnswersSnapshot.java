package com.tomzxy.web_quiz.models.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tomzxy.web_quiz.models.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "answers_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswersSnapshot extends BaseEntity {
    @Column(name = "correct_answer_text", length = 1000)
    private String correctAnswerText;

    @Column(name = "all_answer_options", columnDefinition = "TEXT")
    private String allAnswerOptions;

    @ManyToOne
    @JoinColumn(name = "question_snapshot_id")
    @JsonIgnore
    private QuestionSnapshot questionSnapshot;
}
