package com.tomzxy.web_quiz.configs.security;

import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.enums.LobbyRole;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.LobbyMemberRepo;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("lobbySecurity")
@RequiredArgsConstructor
public class LobbySecurity {

    private final LobbyMemberRepo lobbyMemberRepo;
    private final UserRepo userRepo;
    private final QuizRepo quizRepo;

    public boolean isHost(Long lobbyId, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        LobbyMember member = lobbyMemberRepo
                .findByIdLobbyIdAndIdUserId(lobbyId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("User not in lobby"));

        return member.getRole() == LobbyRole.HOST;
    }

    public boolean isMember(Long lobbyId, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        return lobbyMemberRepo
                .existsByIdLobbyIdAndIdUserId(lobbyId, user.getId());
    }
    public boolean isQuizHost(Long quizId, Authentication authentication) {
    // 1. Kiểm tra xác thực
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
        throw new AccessDeniedException("User chưa đăng nhập");
    }

    String email = authentication.getName(); 
    // 2. Tìm User và kiểm tra quyền Host
    return userRepo.findByEmail(email)
            .map(user -> {
                boolean result = quizRepo.isQuizHost(quizId, user.getId());
                System.err.println("DEBUG: User " + email + " (ID: " + user.getId() + ") check Host cho Quiz " + quizId + " => " + result);
                return result;
            })
            .orElseGet(() -> {
                System.err.println("DEBUG: Không tìm thấy User trong DB với email: " + email);
                return false;
            });
}
}
