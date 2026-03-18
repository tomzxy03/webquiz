package com.tomzxy.web_quiz.models.snapshot;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * Represents a user's answer stored temporarily in Redis.
 * Stored as JSON value in Redis Hash: quiz_instance:{id}:answers
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRecord {
    private List<Integer> answer; // 0-based option indices
    private Instant answeredAt;
}
