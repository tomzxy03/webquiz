package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizAvailable {
    @JsonProperty(value = "anytime")
    ANYTIME,
    @JsonProperty(value = "scheduled")
    SCHEDULED
}
