package com.tomzxy.web_quiz.dto.requests.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupListReqDTO {
    String id;

    @NotNull(message = "user name must be not null")
    private String userName;

    private String profilePictureUrl;
}
