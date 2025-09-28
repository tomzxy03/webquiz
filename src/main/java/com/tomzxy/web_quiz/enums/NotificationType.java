package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationType {
    @JsonProperty(value = "system")
    SYSTEM,
    @JsonProperty(value = "group")
    GROUP,
    @JsonProperty(value = "personal")
    PERSONAL
}
