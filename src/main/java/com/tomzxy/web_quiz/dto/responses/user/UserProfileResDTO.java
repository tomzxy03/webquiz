package com.tomzxy.web_quiz.dto.responses.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResDTO {
    Long id;
    String email;
    String username;
    String fullName;
    String avatar;
    String bio;
    boolean isPublicProfile;
    int totalQuizzesTaken;
    int totalPoints;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}