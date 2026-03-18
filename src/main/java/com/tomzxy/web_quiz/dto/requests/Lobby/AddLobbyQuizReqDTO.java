package com.tomzxy.web_quiz.dto.requests.Lobby;

import lombok.Getter;

import com.tomzxy.web_quiz.models.Quiz.Quiz;

import jakarta.validation.constraints.NotBlank;  

@Getter
public class AddLobbyQuizReqDTO { // add quiz to group

    @NotBlank(message = "Group ID is required")
    private Long groupId;
    // create quiz and add to group
    @NotBlank(message = "Quiz ID is required")
    private Quiz quiz;
}
