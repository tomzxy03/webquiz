package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizType {
    @JsonProperty("public")
    PUBLIC,
    @JsonProperty("group")
    GROUP
}
