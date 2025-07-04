package com.tomzxy.web_quiz.utils;


import lombok.Getter;
import lombok.RequiredArgsConstructor;


public class RegexContains {

    public static final String Email = "/^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/";
    public static final String Phone ="^(84|0)(3|5|7|8|9)[0-9]{8}$";

    private RegexContains(){}
}
