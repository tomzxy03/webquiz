package com.tomzxy.web_quiz.dto.requests.Lobby;

import com.tomzxy.web_quiz.models.Quiz;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;  

@Getter
public class AddQuizReqDTO { // add quiz to group

    @NotBlank(message = "Group ID is required")
    private Long groupId;
    // create quiz and add to group
    @NotBlank(message = "Quiz ID is required")
    private Quiz quiz;
}
