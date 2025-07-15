package com.tomzxy.web_quiz.dto.responses;


import com.tomzxy.web_quiz.enums.AppCode;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.service.annotation.GetExchange;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataResDTO<T> {
    int code;
    String message;
    T items;

    public DataResDTO(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> DataResDTO<T> ok(T data){
        return DataResDTO.<T>builder()
                .code(AppCode.SUCCESS.getCode())
                .message(AppCode.SUCCESS.getMessage())
                .items(data)
                .build();
    }
    public static <T> DataResDTO<T> create(T data){
        return DataResDTO.<T>builder()
                .code(AppCode.CREATED.getCode())
                .message(AppCode.CREATED.getMessage())
                .items(data)
                .build();
    }
    public static <T> DataResDTO<T> create(){
        return DataResDTO.<T>builder()
                .code(AppCode.CREATED.getCode())
                .message(AppCode.CREATED.getMessage())
                .build();
    }
    public static <T> DataResDTO<T> update(T data){
        return DataResDTO.<T>builder()
                .code(AppCode.UPDATE.getCode())
                .message(AppCode.UPDATE.getMessage())
                .items(data)
                .build();
    }
    public static <T> DataResDTO<T> delete(){
        return DataResDTO.<T>builder()
                .code(AppCode.DELETE.getCode())
                .message(AppCode.DELETE.getMessage())
                .build();
    }

    public static <T> DataResDTO<T> error(AppCode appCode){
        return DataResDTO.<T>builder()
                .code(appCode.getCode())
                .message(appCode.getMessage())
                .items(null)
                .build();
    }
    public static <T> DataResDTO<T> error(AppCode appCode , String customMessage){
        return DataResDTO.<T>builder()
                .code(appCode.getCode())
                .message(customMessage)
                .items(null)
                .build();
    }
}
