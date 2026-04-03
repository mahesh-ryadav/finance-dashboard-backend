package com.finance.dashboard.controller;

import com.finance.dashboard.dto.response.*;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Analytics and summary APIs")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    //GET /api/v1/dashboard/summary
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get total income, expense and net balance")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {

        DashboardSummaryResponse response = dashboardService.getSummary();
        return ResponseEntity.ok(
                ApiResponse.success("Dashboard summary fetched successfully", response));
    }

    // GET /api/v1/dashboard/category-summary
    @GetMapping("/category-summary")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get total amount grouped by category")
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> getCategorySummary(
            @RequestParam(required = false) TransactionType type) {

        List<CategorySummaryResponse> response = dashboardService.getCategorySummary(type);
        return ResponseEntity.ok(
                ApiResponse.success("Category summary fetched successfully", response));
    }

    // GET /api/v1/dashboard/monthly-trends
    @GetMapping("/monthly-trends")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get month-wise income vs expense for a year")
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> getMonthlyTrends(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}")
            int year) {

        List<MonthlyTrendResponse> response = dashboardService.getMonthlyTrends(year);
        return ResponseEntity.ok(
                ApiResponse.success("Monthly trends fetched successfully", response));
    }

    // GET /api/v1/dashboard/recent-activity
    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get most recent financial records")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit) {

        List<RecordResponse> response = dashboardService.getRecentActivity(limit);
        return ResponseEntity.ok(
                ApiResponse.success("Recent activity fetched successfully", response));
    }
}
