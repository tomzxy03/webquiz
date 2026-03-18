package com.tomzxy.web_quiz.dto.responses.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLobbyResDTO {
    private Long id;
    private String userName;
    private String email;
    private String avatar;

}
