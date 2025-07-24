package com.tomzxy.web_quiz.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;


@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResultResDTO {
    private Long id;
    private int score;
    private int totalCorrected;
    private int totalFailed;
    private int totalSkipped;
    private Long userId;
    private List<QuizAttemptResDTO> attempts;
} 