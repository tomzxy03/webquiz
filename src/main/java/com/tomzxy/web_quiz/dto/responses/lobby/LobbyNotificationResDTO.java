package com.tomzxy.web_quiz.dto.responses.lobby;


import com.tomzxy.web_quiz.dto.responses.Notification.NotificationResDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LobbyNotificationResDTO {
    Long id;
    private List<NotificationResDTO> notificationResDTOS;

}
