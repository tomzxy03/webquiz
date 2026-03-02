package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IdentityType {
    @JsonProperty(value = "user")
    USER,
    @JsonProperty(value = "guest")
    GUEST
}
