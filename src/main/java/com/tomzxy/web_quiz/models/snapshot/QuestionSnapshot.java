package com.tomzxy.web_quiz.models.snapshot;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
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

    private String content;          // nội dung câu hỏi
    private QuestionAndAnswerType type;             // TEXT, IMAGE, ...

    private Integer points;

    private List<AnswerSnapshot> answers = new ArrayList<>();

    // RẤT QUAN TRỌNG
    private List<String> correctAnswerIds = new ArrayList<>();
}
