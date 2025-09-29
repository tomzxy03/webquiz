package com.tomzxy.web_quiz.dto.requests.Notification;


import com.tomzxy.web_quiz.enums.NotificationType;
import com.tomzxy.web_quiz.validation.EnumValidate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NotificationReqDTO {
    private String title;

    @NotBlank(message = "Content must be not blank")
    private String content;

    private Long lobbyId;

    @EnumValidate(name = "notificationType", regex = "SYSTEM|GROUP|PERSONAL")
    private NotificationType type;

    @NotNull(message = "host must be not null")
    private Long hostId;
}
