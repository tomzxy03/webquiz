package com.tomzxy.web_quiz.utils;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;
import com.tomzxy.web_quiz.repositories.QuizUserResponseRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

    @Component
@RequiredArgsConstructor
@Slf4j
public class GuestCutSchedule {

    private final QuizInstanceRepo quizInstanceRepo;
    private final QuizUserResponseRepo userResponseRepo;

    // Chạy mỗi 30 phút một lần
    @Scheduled(cron = "0 0/30 * * * ?")
    @Transactional
    public void cleanupGuestData() {
        // Mốc thời gian: 4 tiếng cho bài chưa xong, 30 phút cho bài đã xong
        LocalDateTime abandonedCutoff = LocalDateTime.now().minusHours(4);
        LocalDateTime completedCutoff = LocalDateTime.now().minusMinutes(30);

        log.info("Starting guest data cleanup...");

        // 1. Xóa tất cả Responses của các Instance thuộc diện cần xóa
        int deletedResponses = userResponseRepo.deleteResponsesByGuestCriteria(abandonedCutoff, completedCutoff);
        
        // 2. Xóa các QuizInstance của Guest
        int deletedInstances = quizInstanceRepo.deleteGuestInstancesByCriteria(abandonedCutoff, completedCutoff);

        if (deletedInstances > 0) {
            log.info("Cleanup finished: Deleted {} instances and {} responses", deletedInstances, deletedResponses);
        }
    }
}

