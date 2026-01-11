package com.tomzxy.web_quiz.models.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSnapshot {

    private String key;              // "Q1", UUID, hoặc hash
    private Integer order;           // thứ tự sau shuffle

    private String content;          // nội dung câu hỏi
    private String type;             // TEXT, IMAGE, ...
    private String level;            // EASY / MEDIUM / HARD

    private Integer points;

    private List<AnswerSnapshot> answers = new ArrayList<>();

    // RẤT QUAN TRỌNG
    private List<String> correctAnswerIds = new ArrayList<>();
}
