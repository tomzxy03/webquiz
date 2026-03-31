package com.tomzxy.web_quiz.configs.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.tomzxy.web_quiz.containts.ApiDefined.Auth;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;

import lombok.RequiredArgsConstructor;

@Component("attemptSecurity")
@RequiredArgsConstructor
public class AttemptSecurity {

    private final QuizInstanceRepo quizInstanceRepo;
    private final LobbySecurity lobbySecurity;

    public boolean canAccess(Long quizInstanceId, Authentication auth) {
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        Long userId = principal.id();

        QuizInstance instance = quizInstanceRepo.findFullById(quizInstanceId)
            .orElseThrow(() -> new NotFoundException("Not found"));

        boolean isOwner = instance.getUser() != null &&
                          instance.getUser().getId().equals(userId);

        Long groupId = instance.getQuiz().getLobby().getId();
        boolean isHost = lobbySecurity.isHost(groupId, auth);

        return isOwner || isHost;
    }
}
