package com.tomzxy.web_quiz.exception;

import com.tomzxy.web_quiz.enums.AppCode;

public class QuizAttemptException extends ApiException {

    public QuizAttemptException(AppCode appCode) {
        super(appCode);
    }

    public QuizAttemptException(AppCode appCode, String message) {
        super(appCode, message);
    }

    public QuizAttemptException(String message) {
        super(AppCode.INTERNAL_ERROR, message);
    }

    // Specific quiz attempt exceptions
    public static class InvalidAttemptException extends QuizAttemptException {
        public InvalidAttemptException(String message) {
            super("Invalid quiz attempt: " + message);
        }
    }

    public static class AttemptInProgressException extends QuizAttemptException {
        public AttemptInProgressException(String message) {
            super("Attempt already in progress: " + message);
        }
    }

    public static class AttemptCompletedException extends QuizAttemptException {
        public AttemptCompletedException(String message) {
            super("Attempt already completed: " + message);
        }
    }

    public static class AttemptExpiredException extends QuizAttemptException {
        public AttemptExpiredException(String message) {
            super("Attempt expired: " + message);
        }
    }

    public static class MaxAttemptsReachedException extends QuizAttemptException {
        public MaxAttemptsReachedException(String message) {
            super("Maximum attempts reached: " + message);
        }
    }

    public static class CannotResumeException extends QuizAttemptException {
        public CannotResumeException(String message) {
            super("Cannot resume attempt: " + message);
        }
    }
} 