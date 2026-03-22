package com.tomzxy.web_quiz.dto.requests.admin;

import com.tomzxy.web_quiz.enums.AdminGroupStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminGroupUpdateReqDTO {
    private AdminGroupStatus status;
}
