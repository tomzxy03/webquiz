package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AnswerType {
    @JsonProperty(value = "single_choice")
    SINGLE_CHOICE,     // radio
    @JsonProperty(value = "multiple_choice")
    MULTIPLE_CHOICE,   // checkbox
    @JsonProperty(value = "true_false")
    TRUE_FALSE,
    @JsonProperty(value = "short_answer")
    SHORT_ANSWER,
    @JsonProperty(value = "fill_in_blank")
    FILL_IN_BLANK,
    @JsonProperty(value = "matching")
    MATCHING
}