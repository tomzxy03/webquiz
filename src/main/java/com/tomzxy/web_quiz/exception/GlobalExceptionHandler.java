package com.tomzxy.web_quiz.exception;


import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public DataResDTO<Object> handleApiException(ApiException apiException){
        return DataResDTO.error(apiException.getAppCode(), apiException.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataResDTO<Object> handlerValidation(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField()+ ": "+ e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return DataResDTO.error(AppCode.BAD_REQUEST, message);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResDTO<?>> handleUnknown(Exception ex) {
        DataResDTO<?> res = DataResDTO.error(AppCode.UNKNOWN_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON) // bắt buộc JSON
                .body(res);
    }
    
}
