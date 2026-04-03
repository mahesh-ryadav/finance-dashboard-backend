package com.finance.dashboard.controller;

import com.finance.dashboard.dto.request.CreateRecordRequest;
import com.finance.dashboard.dto.request.UpdateRecordRequest;
import com.finance.dashboard.dto.response.ApiResponse;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "CRUD and filtering for financial records")
@SecurityRequirement(name = "bearerAuth")
public class RecordController {

    private final RecordService recordService;

    // GET /api/v1/records
    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get all records with optional filters and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<RecordResponse>>> getAllRecords(

            // Filter parameters
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            // Pagination parameters
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<RecordResponse> response = recordService.getAllRecords(
                type, category, startDate, endDate, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Records fetched successfully", response));
    }

    //GET /api/v1/records/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get a single record by ID")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecordById(
            @PathVariable Long id) {

        RecordResponse response = recordService.getRecordById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Record fetched successfully", response));
    }

    // POST /api/v1/records
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new financial record")
    public ResponseEntity<ApiResponse<RecordResponse>> createRecord(
            @Valid @RequestBody CreateRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        RecordResponse response = recordService.createRecord(
                request,
                userDetails.getUsername()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Record created successfully", response));
    }

    // PUT /api/v1/records/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Full update of a financial record")
    public ResponseEntity<ApiResponse<RecordResponse>> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecordRequest request) {

        RecordResponse response = recordService.updateRecord(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Record updated successfully", response));
    }

    // PATCH /api/v1/records/{id}
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Partial update of a financial record")
    public ResponseEntity<ApiResponse<RecordResponse>> partialUpdateRecord(
            @PathVariable Long id,
            @RequestBody UpdateRecordRequest request) {

        RecordResponse response = recordService.partialUpdateRecord(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Record partially updated successfully", response));
    }

    // DELETE /api/v1/records/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a financial record")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(
            @PathVariable Long id) {

        recordService.deleteRecord(id);
        return ResponseEntity.ok(
                ApiResponse.success("Record deleted successfully"));
    }
}