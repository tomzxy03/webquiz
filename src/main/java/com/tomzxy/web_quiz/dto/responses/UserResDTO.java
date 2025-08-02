package com.tomzxy.web_quiz.dto.responses;


import com.tomzxy.web_quiz.enums.Gender;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;


@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResDTO {
    Long id;
    String userName;
    String phone;
    String email;
    Gender gender;
    String dateOfBirth;
    Set<String> roles;
}
