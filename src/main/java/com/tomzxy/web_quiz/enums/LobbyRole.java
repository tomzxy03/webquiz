package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LobbyRole {
    @JsonProperty(value = "host")
    HOST,
    @JsonProperty(value = "member")
    MEMBER;
}
