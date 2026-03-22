package com.tomzxy.web_quiz.dto.requests.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminListReqDTO {
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy;
    private String direction; // ASC | DESC
    private String search;
    private String status;
    private String visibility;
    private Long groupId;
    private Long quizId;
    private Long userId;
}
