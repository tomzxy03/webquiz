package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuestionAndAnswerType {
    @JsonProperty(value = "text")
    TEXT,
    @JsonProperty(value = "image")
    IMAGE
}
