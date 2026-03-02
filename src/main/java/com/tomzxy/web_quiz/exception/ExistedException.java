package com.tomzxy.web_quiz.exception;

import com.tomzxy.web_quiz.enums.AppCode;

public class ExistedException extends ApiException{
    public ExistedException(AppCode appCode, String message){
        super(appCode, message);
    }
}
