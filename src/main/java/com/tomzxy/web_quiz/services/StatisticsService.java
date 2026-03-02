package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.responses.GlobalStatisticsResDTO;
import com.tomzxy.web_quiz.dto.responses.UserStatisticsResDTO;

public interface StatisticsService {
    UserStatisticsResDTO getUserStatistics(Long userId);

    GlobalStatisticsResDTO getGlobalStatistics();
}
