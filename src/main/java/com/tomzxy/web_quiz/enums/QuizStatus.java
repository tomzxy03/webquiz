package com.tomzxy.web_quiz.enums;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizStatus {
    @JsonProperty(value = "draft")
    DRAFT,       // Đang tạo, chưa publish
    @JsonProperty(value = "public")
    PUBLISHED,   // Đã publish, có thể join
    @JsonProperty(value = "closed")
    CLOSED,   // Đã hết thời gian
    @JsonProperty(value = "archived")
    ARCHIVED     // Đã archive
}
