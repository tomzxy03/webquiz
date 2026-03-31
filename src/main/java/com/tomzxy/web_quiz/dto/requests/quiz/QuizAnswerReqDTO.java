package com.tomzxy.web_quiz.dto.requests.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizAnswerReqDTO {

    @NotBlank(message = "Question snapshot key is required")
    private String questionSnapshotKey;

    @NotNull(message = "Answer is required")
    @NotEmpty(message = "Answer cannot be empty")
    private List<Integer> answer; // 0-based option indices
}
