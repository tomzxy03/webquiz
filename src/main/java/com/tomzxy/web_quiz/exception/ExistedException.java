package com.tomzxy.web_quiz.exception;

import com.tomzxy.web_quiz.enums.AppCode;

public class ExistedException extends ApiException{
    public ExistedException(String message){
        super(AppCode.DATA_EXISTED, message);
    }
}
