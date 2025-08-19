package com.tomzxy.web_quiz.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import com.tomzxy.web_quiz.containts.ApiDefined;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Quiz.BASE)
public class QuizController {
    
}
