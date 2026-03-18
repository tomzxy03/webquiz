package com.tomzxy.web_quiz.configs;

import com.tomzxy.web_quiz.services.QuizInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task that checks for timed-out quiz instances
 * and auto-submits them using the Redis Sorted Set approach.
 *
 * Runs every 15 seconds, processing up to 100 instances per run.
 * Uses distributed locks to prevent multiple workers processing the same
 * instance.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuizTimeoutScheduler {

    private final QuizInstanceService quizInstanceService;

    @Scheduled(fixedRate = 15000) // every 15 seconds
    public void checkTimeouts() {
        try {
            quizInstanceService.handleTimedOutInstances();
        } catch (Exception e) {
            log.error("Error in timeout scheduler", e);
        }
    }
}
