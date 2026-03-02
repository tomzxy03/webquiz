package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizVisibility {
    @JsonProperty("public")
    PUBLIC,      // Ai cũng thấy
    @JsonProperty("private")
    PRIVATE,     // Chỉ host thấy
    @JsonProperty("class_only")
    CLASS_ONLY   // Chỉ trong class
}
