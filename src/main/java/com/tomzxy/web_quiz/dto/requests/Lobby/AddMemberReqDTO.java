package com.tomzxy.web_quiz.dto.requests.Lobby;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class AddMemberReqDTO { // add list member to group
    @NotBlank(message = "Group ID is required")
    private Long groupId;

    @NotEmpty(message = "Member ID is required")
    private List<Long> memberIds;
}
