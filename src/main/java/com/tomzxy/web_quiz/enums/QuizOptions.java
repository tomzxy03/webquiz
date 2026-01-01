package com.tomzxy.web_quiz.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuizOptions {
    @JsonProperty("shuffle_questions")
    SHUFFLE_QUESTIONS,
    @JsonProperty("shuffle_answers")
    SHUFFLE_ANSWERS,
    @JsonProperty("allow_review")
    ALLOW_REVIEW,
    @JsonProperty("show_score_after_submit")
    SHOW_SCORE_AFTER_SUBMIT,
    @JsonProperty("allow_retry")
    ALLOW_RETRY
} 