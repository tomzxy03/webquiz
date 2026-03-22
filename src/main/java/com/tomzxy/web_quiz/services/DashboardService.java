package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.responses.dashboard.DashboardSummaryResDTO;

public interface DashboardService {
    DashboardSummaryResDTO getSummary(Long userId);
}
