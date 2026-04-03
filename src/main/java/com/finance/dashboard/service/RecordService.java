package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.CreateRecordRequest;
import com.finance.dashboard.dto.request.UpdateRecordRequest;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.mapper.RecordMapper;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository            userRepository;

    // Get all records with optional filters
    @Transactional(readOnly = true)
    public PagedResponse<RecordResponse> getAllRecords(
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        Page<FinancialRecord> records;

        //Route to correct repository method based on filters provided
        if (type != null && category != null) {
            records = recordRepository.findByTypeAndCategory(type, category, pageable);

        } else if (type != null && startDate != null && endDate != null) {
            records = recordRepository.findByTypeAndDateRange(type, startDate, endDate, pageable);

        } else if (type != null) {
            records = recordRepository.findByType(type, pageable);

        } else if (category != null) {
            records = recordRepository.findByCategory(category, pageable);

        } else if (startDate != null && endDate != null) {
            records = recordRepository.findByDateRange(startDate, endDate, pageable);

        } else {
            records = recordRepository.findAllActive(pageable);
        }

        return RecordMapper.toPagedResponse(records);
    }

    // Get single record by ID
    @Transactional(readOnly = true)
    public RecordResponse getRecordById(Long id) {
        FinancialRecord record = findRecordById(id);
        return RecordMapper.toResponse(record);
    }

    // Create record
    @Transactional
    public RecordResponse createRecord(CreateRecordRequest request, String userEmail) {

        // Find logged-in user to set as createdBy
        User creator = userRepository
                .findByEmailAndIsDeletedFalse(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        // Map request to entity
        FinancialRecord record = RecordMapper.toEntity(request);

        // Set creator
        record.setCreatedBy(creator);

        FinancialRecord savedRecord = recordRepository.save(record);
        return RecordMapper.toResponse(savedRecord);
    }

    // Full update record (PUT)
    @Transactional
    public RecordResponse updateRecord(Long id, UpdateRecordRequest request) {
        FinancialRecord record = findRecordById(id);

        // Update all fields
        RecordMapper.updateEntity(record, request);

        FinancialRecord updatedRecord = recordRepository.save(record);
        return RecordMapper.toResponse(updatedRecord);
    }

    // Partial update record (PATCH)
    @Transactional
    public RecordResponse partialUpdateRecord(Long id, UpdateRecordRequest request) {
        FinancialRecord record = findRecordById(id);

        RecordMapper.updateEntity(record, request);

        FinancialRecord updatedRecord = recordRepository.save(record);
        return RecordMapper.toResponse(updatedRecord);
    }

    // Soft delete record
    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = findRecordById(id);
        record.setIsDeleted(true);
        recordRepository.save(record);
    }

    // Private helper
    private FinancialRecord findRecordById(Long id) {
        return recordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialRecord", "id", id));
    }
}