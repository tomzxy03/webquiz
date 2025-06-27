package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Gender {
    @JsonProperty(value = "male")
    MALE,
    @JsonProperty(value = "female")
    FEMALE,
    @JsonProperty(value = "other")
    OTHER

}
