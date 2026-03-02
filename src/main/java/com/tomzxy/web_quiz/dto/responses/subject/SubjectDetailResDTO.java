package com.tomzxy.web_quiz.dto.responses.subject;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectDetailResDTO {
    Long id;
    String subjectName;
    String description;
    Long countQuiz;
}
