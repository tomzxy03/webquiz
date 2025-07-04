package com.tomzxy.web_quiz.dto.responses;


import com.tomzxy.web_quiz.enums.Gender;
import com.tomzxy.web_quiz.models.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResDTO {
    Long id;
    String user_name;
    String phone;
    String email;
    Gender gender;
    String dob;
    Set<String> roles;
}
