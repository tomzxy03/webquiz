package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizVisibility {
    PUBLIC,      // Ai cũng thấy
    PRIVATE,     // Chỉ host thấy
    CLASS_ONLY   // Chỉ trong class
}
