package com.tomzxy.web_quiz.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EnumValidator implements ConstraintValidator<EnumValidate, Enum<?>> {
    private Pattern pattern;


    @Override
    public void initialize(EnumValidate enumValidate) {
        try{
            pattern = Pattern.compile(enumValidate.regex());
        }catch (PatternSyntaxException e){
            throw new IllegalArgumentException("Given regex is invalid ", e);
        }
    }

    @Override
    public boolean isValid(Enum<?> anEnum, ConstraintValidatorContext constraintValidatorContext) {
        if(anEnum == null) return true;
        Matcher matcher = pattern.matcher(anEnum.name());
        return matcher.matches();
    }
}
