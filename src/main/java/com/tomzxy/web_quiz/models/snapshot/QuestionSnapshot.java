package com.tomzxy.web_quiz.models.snapshot;

import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter


public class QuestionSnapshot{
    private String key;              // "Q1", UUID, hoặc hash
    private String content;          // nội dung câu hỏi
    @Enumerated(EnumType.STRING)
    private QuestionAndAnswerType type;             // TEXT, IMAGE, ...
    private Integer orderIndex;
    private Long points;
    private List<AnswerSnapshot> answers = new ArrayList<>();

    private List<String> correctAnswerIds = new ArrayList<>();
}
