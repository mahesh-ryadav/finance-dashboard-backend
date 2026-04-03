package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.CategorySummaryResponse;
import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.MonthlyTrendResponse;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.mapper.RecordMapper;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    // Dashboard Summary
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {

        BigDecimal totalIncome  = recordRepository
                .sumAmountByType(TransactionType.INCOME);

        BigDecimal totalExpense = recordRepository
                .sumAmountByType(TransactionType.EXPENSE);

        if (totalIncome  == null) totalIncome  = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .totalRecords(recordRepository.count())
                .build();
    }

    // Category Summary
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getCategorySummary(TransactionType type) {

        List<Object[]> rows;

        if (type != null) {
            rows = recordRepository.findCategorySummaryByType(type);
        } else {
            rows = recordRepository.findCategorySummary();
        }

        return RecordMapper.toCategorySummaryList(rows);
    }

    // Monthly Trends
    @Transactional(readOnly = true)
    public List<MonthlyTrendResponse> getMonthlyTrends(int year) {

        List<Object[]> rows = recordRepository.findMonthlyTrends(year);



        // Step 1: Build a map for month to {income, expense}
        Map<Integer, BigDecimal> incomeMap  = new HashMap<>();
        Map<Integer, BigDecimal> expenseMap = new HashMap<>();

        for (Object[] row : rows) {
            int    month  = ((Number) row[0]).intValue();
            String type   = row[1].toString();
            BigDecimal amt = (BigDecimal) row[2];

            if (type.equals("INCOME")) {
                incomeMap.put(month, amt);
            } else {
                expenseMap.put(month, amt);
            }
        }

        // Step 2: Build response for all 12 months
        List<MonthlyTrendResponse> trends = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            BigDecimal income  = incomeMap.getOrDefault(month,  BigDecimal.ZERO);
            BigDecimal expense = expenseMap.getOrDefault(month, BigDecimal.ZERO);
            trends.add(RecordMapper.toMonthlyTrend(month, income, expense));
        }

        return trends;
    }

    // Recent Activity
    @Transactional(readOnly = true)
    public List<RecordResponse> getRecentActivity(int limit) {

        // Clamp limit between 1 and 50
        int safeLimit = Math.min(Math.max(limit, 1), 50);

        Pageable pageable = PageRequest.of(
                0,
                safeLimit,
                Sort.by("date").descending()
        );

        return recordRepository.findRecentRecords(pageable)
                .stream()
                .map(RecordMapper::toResponse)
                .collect(Collectors.toList());
    }
}
