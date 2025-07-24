package com.tomzxy.web_quiz.dto.requests.Lobby;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LobbyReqDTO {
    @NotBlank(message = "Group name is required")
    private String groupName;

    private Set<Long> members;

    private List<Long> quizzes;
}
