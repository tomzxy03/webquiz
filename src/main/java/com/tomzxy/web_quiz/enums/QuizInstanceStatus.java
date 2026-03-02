package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizInstanceStatus {
    @JsonProperty("in_progress")
    IN_PROGRESS,    // Đang tham gia
    @JsonProperty("submitted")
    SUBMITTED,      // Đã submit
    @JsonProperty("timed_out")
    TIMED_OUT,      // Hết thời gian
    @JsonProperty("abandoned")
    ABANDONED       // Bỏ dở
} 