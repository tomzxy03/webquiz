package com.tomzxy.web_quiz.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppCode {
    SUCCESS(200, "Success"),
    CREATED(200, "Create successfully"),
    UPDATE(200, "Update successfully"),
    DELETE(204, "Delete successfully"),
    NOT_FOUND(404 , "Resource not found"),
    BAD_REQUEST(400 , "Bad request"),
    UNAUTHORIZED(401 , "Unauthorized"),
    FORBIDDEN(403 , "Forbidden"),
    INTERNAL_ERROR(500 , "Internal server error"),
    DATA_EXISTED(409, "Data already exists");

    private final int code;
    private final String message;
}
