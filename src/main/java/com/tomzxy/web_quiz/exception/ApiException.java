package com.tomzxy.web_quiz.exception;


import com.tomzxy.web_quiz.enums.AppCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final AppCode appCode;

    public ApiException(AppCode appCode){
        super(appCode.getMessage());
        this.appCode=appCode;
    }
    public ApiException(AppCode appCode, String message){
        super(message);
        this.appCode=appCode;
    }
    public ApiException(AppCode appCode, String message, Throwable cause) {
        super(message, cause);
        this.appCode = appCode;
    }

    public ApiException(AppCode appCode, Throwable cause) {
        super(appCode.getMessage(), cause);
        this.appCode = appCode;
    }

    public int getCode() {
        return appCode.getCode();
    }

    public String getDefaultMessage() {
        return appCode.getMessage();
    }

    public org.springframework.http.HttpStatus getHttpStatus() {
        return appCode.getHttpStatus();
    }

}
