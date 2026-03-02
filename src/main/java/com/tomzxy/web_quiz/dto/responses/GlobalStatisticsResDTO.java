package com.tomzxy.web_quiz.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GlobalStatisticsResDTO {
    long totalUsers;
    long totalQuizzes;
    long totalAttempts;
    long totalGroups;
    long activeUsers;
    long activeQuizzes;
}
