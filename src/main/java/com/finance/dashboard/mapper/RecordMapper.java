package com.finance.dashboard.mapper;

import com.finance.dashboard.dto.request.CreateRecordRequest;
import com.finance.dashboard.dto.request.UpdateRecordRequest;
import com.finance.dashboard.dto.response.CategorySummaryResponse;
import com.finance.dashboard.dto.response.MonthlyTrendResponse;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.entity.FinancialRecord;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

public class RecordMapper {

    // Mapper method for FinancialRecord to RecordResponse dto
    public static RecordResponse toResponse(FinancialRecord record) {
        if (record == null) return null;

        return RecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .createdByName(
                        record.getCreatedBy() != null
                                ? record.getCreatedBy().getName()
                                : "Unknown"
                )
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    // mapper method for CreateRecordRequest to Entity

    public static FinancialRecord toEntity(CreateRecordRequest request) {
        if (request == null) return null;

        return FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory().trim())
                .date(request.getDate())
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .isDeleted(false)
                .build();
    }

//    method to update financial record (Patch support)
    public static void updateEntity(FinancialRecord existing, UpdateRecordRequest request) {
        if (request == null) return;

        if (request.getAmount() != null) {
            existing.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            existing.setType(request.getType());
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            existing.setCategory(request.getCategory().trim());
        }
        if (request.getDate() != null) {
            existing.setDate(request.getDate());
        }
        if (request.getNotes() != null) {
            existing.setNotes(request.getNotes().trim());
        }
    }

    // paged FinancialRecord to RecordResponse
    public static PagedResponse<RecordResponse> toPagedResponse(Page<FinancialRecord> page) {
        List<RecordResponse> content = page.getContent()
                .stream()
                .map(RecordMapper::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<RecordResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

//   Category wise record summary in row column form
    public static CategorySummaryResponse toCategorySummary(Object[] row) {
        return CategorySummaryResponse.builder()
                .category((String) row[0])
                .totalAmount((BigDecimal) row[1])
                .build();
    }

    // List of record summary
    public static List<CategorySummaryResponse> toCategorySummaryList(List<Object[]> rows) {
        return rows.stream()
                .map(RecordMapper::toCategorySummary)
                .collect(Collectors.toList());
    }

 // mapper to get monthly trends
    public static MonthlyTrendResponse toMonthlyTrend(
            int month,
            BigDecimal income,
            BigDecimal expense) {

        BigDecimal net = income.subtract(expense);
        String monthName = Month.of(month).name().charAt(0)
                + Month.of(month).name().substring(1).toLowerCase();

        return MonthlyTrendResponse.builder()
                .month(month)
                .monthName(monthName)
                .income(income)
                .expense(expense)
                .net(net)
                .build();
    }
}
