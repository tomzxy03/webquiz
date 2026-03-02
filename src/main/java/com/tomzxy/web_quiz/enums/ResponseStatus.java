package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResponseStatus {
    @JsonProperty("skipped")
    SKIPPED,
    @JsonProperty("correct")
    CORRECT,
    @JsonProperty("incorrect")
    INCORRECT
}
