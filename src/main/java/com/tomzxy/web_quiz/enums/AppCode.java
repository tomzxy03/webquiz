package com.tomzxy.web_quiz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AppCode {
    SUCCESS(200, "Success", HttpStatus.OK),
    CREATED(201, "Create successfully", HttpStatus.CREATED),
    UPDATE(200, "Update successfully", HttpStatus.OK),
    DELETE(204, "Delete successfully", HttpStatus.NO_CONTENT),
    NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    BAD_REQUEST(400, "Bad request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "Forbidden", HttpStatus.FORBIDDEN),
    INTERNAL_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_EXISTED(409, "Data already exists", HttpStatus.CONFLICT),
    CONFLICT(409, "Conflict", HttpStatus.CONFLICT),
    USER_NOT_ELIGIBLE(403, "User not eligible", HttpStatus.FORBIDDEN),
    NOT_AVAILABLE(404, "Not available", HttpStatus.NOT_FOUND),
    NOT_PERMISSION(401, "Not be permission", HttpStatus.UNAUTHORIZED),
    UNKNOWN_ERROR(500, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_ALREADY_REGISTERED(409, "Email already registered", HttpStatus.CONFLICT),
    USERNAME_ALREADY_TAKEN(409, "Username already taken", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(401, "Invalid credentials", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

}
