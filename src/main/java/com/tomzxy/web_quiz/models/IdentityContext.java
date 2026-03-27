package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.enums.IdentityType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityContext {
    private IdentityType type;
    private Long userId;
    private String guestId;

    public IdentityContext(IdentityType type, String id) {
        this.type = type;
        if (type == IdentityType.USER) {
            this.userId = Long.parseLong(id);
        } else if (type == IdentityType.GUEST) {
            this.guestId = id;
        }
    }
}
