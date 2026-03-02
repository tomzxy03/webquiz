package com.tomzxy.web_quiz.configs;

import com.tomzxy.web_quiz.models.IdentityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IdentityResolver {

    public IdentityContext resolve(HttpServletRequest request) {
        if (hasValidJwt(request)) {
            return userIdentity();
        }
        if (hasGuestToken(request)) {
            return guestIdentity();
        }
        return anonymous();
    }

    private boolean hasValidJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private boolean hasGuestToken(HttpServletRequest request) {
        String guestToken = request.getHeader("X-Guest-Token");
        return guestToken != null && !guestToken.isBlank();
    }

    private IdentityContext userIdentity() {
        return new IdentityContext();
    }

    private IdentityContext guestIdentity() {
        return new IdentityContext();
    }

    private IdentityContext anonymous() {
        return new IdentityContext();
    }
}
