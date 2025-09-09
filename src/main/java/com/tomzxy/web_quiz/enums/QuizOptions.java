package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizOptions {
    @JsonProperty("shuffle_questions")
    SHUFFLE_QUESTIONS,
    @JsonProperty("shuffle_answers")
    SHUFFLE_ANSWERS,
    @JsonProperty("allow_review")
    ALLOW_REVIEW
} 