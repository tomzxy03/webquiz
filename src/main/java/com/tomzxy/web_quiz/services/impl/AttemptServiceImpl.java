package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.configs.security.CustomUserDetails;
import com.tomzxy.web_quiz.configs.security.LobbySecurity;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResultDetailResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.IdentityType;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.IdentityContext;
import com.tomzxy.web_quiz.models.Quiz.Quiz;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.QuizRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.AttemptService;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final QuizInstanceService quizInstanceService;
    private final QuizRepo quizRepo;
    private final QuizInstanceRepo quizInstanceRepo;
    private final UserRepo userRepo;
    private final LobbySecurity lobbySecurity;

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getMyAttempts(IdentityContext identity, int page, int size) {
        Long userId = requireUserId(identity);
        return quizInstanceService.getAllQuizInstancesByUserId(userId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResDTO<?> getQuizAttempts(Long quizId, Long groupId, int page, int size) {
        Quiz quiz = quizRepo.findById(quizId)
    .orElseThrow(() -> new NotFoundException("Quiz not found"));

    if (!quiz.getLobby().getId().equals(groupId)) {
        throw new ApiException(AppCode.BAD_REQUEST, "Quiz does not belong to group");
    }
        return quizInstanceService.getAllQuizInstancesByQuizId(quizId, page, size);
    }

    @Override
    @Transactional(readOnly = true)

    public QuizResultDetailResDTO getAttemptDetail(Long instanceId, Authentication auth) {

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        String email = principal.getUsername();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Long userId = user.getId();
        QuizInstance instance = quizInstanceRepo.findFullById(instanceId)
                .orElseThrow(() -> new NotFoundException("Quiz instance not found"));

        boolean isOwner = instance.getUser() != null &&
                instance.getUser().getId().equals(userId);

        if (isOwner) {
            return quizInstanceService.getQuizResult(instanceId, null);
        }

        // Nếu không phải owner → chắc chắn là host (đã check ở @PreAuthorize)
        return quizInstanceService.getQuizResultForHost(instanceId);
    }

    private Long requireUserId(IdentityContext identity) {
        if (identity == null || identity.getType() != IdentityType.USER || identity.getUserId() == null) {
            throw new ApiException(AppCode.UNAUTHORIZED, "User identity is required");
        }
        return identity.getUserId();
    }
}
