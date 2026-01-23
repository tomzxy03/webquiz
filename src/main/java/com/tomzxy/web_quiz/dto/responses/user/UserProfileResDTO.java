package com.tomzxy.web_quiz.dto.responses.user;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserProfileResDTO {
    private Long id;
    private String userName;
    private String phone;
    private String email;
    private Gender gender;

    private String profilePictureUrl;
}