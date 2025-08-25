package com.tomzxy.web_quiz.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswersSnapshot {
    @Column(name = "correct_answer_text", length = 1000)
    private String correctAnswerText;

    @Column(name = "all_answer_options", columnDefinition = "TEXT")
    private String allAnswerOptions;
} 