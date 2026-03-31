package com.tomzxy.web_quiz.configs;

import com.tomzxy.web_quiz.configs.security.CustomUserDetails;
import com.tomzxy.web_quiz.enums.IdentityType;
import com.tomzxy.web_quiz.models.IdentityContext;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentityResolver {
    private final UserRepo userRepo;

    public IdentityContext resolve(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Lấy từ SecurityContext (vì Filter đã parse JWT và đặt vào đó rồi)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
                User userTemp = userRepo.findByEmailAndIsActiveTrue(user.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                return new IdentityContext(IdentityType.USER, String.valueOf(userTemp.getId()));
            }
        }

        // 2. Check Guest Token (Guestxxxxxx)
        String guestToken = request.getHeader("X-Guest-Token"); // Ví dụ: GUEST-12345

        if (guestToken != null && !guestToken.isBlank()) {
            return new IdentityContext(IdentityType.GUEST, guestToken);
        }
        throw new RuntimeException("No valid identity found in request");
    }
}
