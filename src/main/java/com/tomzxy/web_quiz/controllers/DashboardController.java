package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.dashboard.DashboardSummaryResDTO;
import com.tomzxy.web_quiz.services.DashboardService;
import com.tomzxy.web_quiz.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Dashboard.BASE)
@Tag(name = "Dashboard", description = "Dashboard summary APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(ApiDefined.Dashboard.SUMMARY)
    @Operation(summary = "Get dashboard summary", description = "Retrieve dashboard summary for the authenticated user including stats, in-progress quizzes, recent activity, groups, and draft quizzes")
    @PreAuthorize("isAuthenticated()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<DataResDTO<DashboardSummaryResDTO>> getSummary() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Dashboard summary requested for user {}", userId);
        DashboardSummaryResDTO summary = dashboardService.getSummary(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(summary));
    }
}
