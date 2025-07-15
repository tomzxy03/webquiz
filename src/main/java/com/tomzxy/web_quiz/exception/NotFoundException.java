package com.tomzxy.web_quiz.exception;

import com.tomzxy.web_quiz.enums.AppCode;

public class NotFoundException extends ApiException{
    public NotFoundException(String message){
        super(AppCode.NOT_FOUND, message);
    }


}
