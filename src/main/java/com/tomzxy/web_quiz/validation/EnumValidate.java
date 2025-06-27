package com.tomzxy.web_quiz.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.CONSTRUCTOR,ElementType.PARAMETER,ElementType.ANNOTATION_TYPE,ElementType.TYPE_USE})
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumValidate {
    String name();
    String regex();
    String message() default  "{name} must match {regex}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
