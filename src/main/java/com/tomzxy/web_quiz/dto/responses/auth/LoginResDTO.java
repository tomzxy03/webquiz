package com.tomzxy.web_quiz.dto.responses.auth;


import lombok.*;
import lombok.experimental.FieldDefaults;


@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResDTO {
    String token;
}
