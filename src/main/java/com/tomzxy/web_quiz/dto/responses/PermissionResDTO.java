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
public class PermissionResDTO {
    String permissionName;
    String description;

    boolean isActive;
}
