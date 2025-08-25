package com.tomzxy.web_quiz.exception;

import com.tomzxy.web_quiz.enums.AppCode;

public class QuizResultException extends ApiException {

    public QuizResultException(AppCode appCode) {
        super(appCode);
    }

    public QuizResultException(AppCode appCode, String message) {
        super(appCode, message);
    }

    public QuizResultException(String message) {
        super(AppCode.INTERNAL_ERROR, message);
    }

    // Specific quiz result exceptions
    public static class InvalidResultException extends QuizResultException {
        public InvalidResultException(String message) {
            super("Invalid quiz result: " + message);
        }
    }

    public static class ResultCalculationException extends QuizResultException {
        public ResultCalculationException(String message) {
            super("Result calculation error: " + message);
        }
    }

    public static class ResultSubmissionException extends QuizResultException {
        public ResultSubmissionException(String message) {
            super("Result submission error: " + message);
        }
    }

    public static class TimeExpiredException extends QuizResultException {
        public TimeExpiredException(String message) {
            super("Time expired: " + message);
        }
    }

    public static class DuplicateResultException extends QuizResultException {
        public DuplicateResultException(String message) {
            super("Duplicate result: " + message);
        }
    }
} 