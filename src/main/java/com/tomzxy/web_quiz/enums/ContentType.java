package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ContentType {
    @JsonProperty(value = "text")
    TEXT,
    @JsonProperty(value = "image")
    IMAGE,
    @JsonProperty(value = "audio")
    AUDIO,
    @JsonProperty(value = "video")
    VIDEO
}
