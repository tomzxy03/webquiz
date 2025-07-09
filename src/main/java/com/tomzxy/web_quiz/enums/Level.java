package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Level {

    @JsonProperty(value = "easy")
    EASY,
    @JsonProperty(value = "medium")
    MEDIUM,
    @JsonProperty(value = "hard")
    HARD
}
