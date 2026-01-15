package com.tomzxy.web_quiz.dto.responses.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserMemberResDTO {
    private Long id;
    private String userName;
    private String profilePictureUrl;
    private String roleName;

}
