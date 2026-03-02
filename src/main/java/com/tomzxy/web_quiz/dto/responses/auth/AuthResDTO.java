package com.tomzxy.web_quiz.dto.responses.auth;

import com.tomzxy.web_quiz.dto.responses.user.UserResDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResDTO {
    String token;
    String refreshToken;
    UserResDTO user;
    long expiresIn;
}
