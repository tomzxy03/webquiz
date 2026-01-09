package com.tomzxy.web_quiz.enums;


public enum QuizStatus {
    DRAFT,       // Đang tạo, chưa publish
    PUBLISHED,   // Đã publish, có thể join
    ACTIVE,      // Đang có users làm
    COMPLETED,   // Đã hết thời gian
    ARCHIVED     // Đã archive
}
