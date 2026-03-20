package com.tomzxy.web_quiz.dto.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionBankReqDTO {
    String description;
}
