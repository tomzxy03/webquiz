package com.tomzxy.web_quiz.dto.requests.admin;

import com.tomzxy.web_quiz.enums.AdminQuizStatus;
import com.tomzxy.web_quiz.enums.AdminQuizVisibility;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminQuizUpdateReqDTO {
    private AdminQuizStatus status;
    private AdminQuizVisibility visibility;
}
