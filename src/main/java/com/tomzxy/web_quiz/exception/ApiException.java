package com.tomzxy.web_quiz.exception;


import com.tomzxy.web_quiz.enums.AppCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final AppCode appCode;

    public ApiException(AppCode appCode){
        super(appCode.getMessage());
        this.appCode=appCode;
    }
    public ApiException(AppCode appCode, String message){
        super(message);
        this.appCode=appCode;
    }

}
