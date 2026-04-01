package com.finance.dashboard.repository;

import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {


    // All non-deleted records with pagination
    @Query("SELECT r FROM FinancialRecord r WHERE r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findAllActive(Pageable pageable);

    // Single record which is not soft deleted
    @Query("SELECT r FROM FinancialRecord r WHERE r.id = :id AND r.isDeleted = false")
    Optional<FinancialRecord> findByIdAndIsDeletedFalse(@Param("id") Long id);


    // Filtering by type (INCOME or EXPENSE)
    @Query("SELECT r FROM FinancialRecord r WHERE r.type = :type AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByType(
            @Param("type") TransactionType type,
            Pageable pageable);

    // Filter by category
    @Query("SELECT r FROM FinancialRecord r WHERE LOWER(r.category) = LOWER(:category) AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByCategory(
            @Param("category") String category,
            Pageable pageable);

    // Filter by date range
    @Query("SELECT r FROM FinancialRecord r WHERE r.date BETWEEN :startDate AND :endDate AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // Filter by type and category combined
    @Query("SELECT r FROM FinancialRecord r WHERE r.type = :type AND LOWER(r.category) = LOWER(:category) AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByTypeAndCategory(
            @Param("type") TransactionType type,
            @Param("category") String category,
            Pageable pageable);

    // Filter by type and date range combined
    @Query("SELECT r FROM FinancialRecord r WHERE r.type = :type AND r.date BETWEEN :startDate AND :endDate AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByTypeAndDateRange(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);


    // Total income OR total expense
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type AND r.isDeleted = false")
    BigDecimal sumAmountByType(@Param("type") TransactionType type);



    // Category-wise totals used in dashboard
    // It will return List of Object[] like [category, total]
    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.isDeleted = false GROUP BY r.category ORDER BY SUM(r.amount) DESC")
    List<Object[]> findCategorySummary();



    // Category-wise totals filtered by type
    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type AND r.isDeleted = false GROUP BY r.category ORDER BY SUM(r.amount) DESC")
    List<Object[]> findCategorySummaryByType(@Param("type") TransactionType type);



    // Monthly trends for a given year  used in dashboard or monthly-trends
    // It will return List of Object[] like [month, type, total]
    @Query("SELECT MONTH(r.date), r.type, SUM(r.amount) FROM FinancialRecord r WHERE YEAR(r.date) = :year AND r.isDeleted = false GROUP BY MONTH(r.date), r.type ORDER BY MONTH(r.date)")
    List<Object[]> findMonthlyTrends(@Param("year") int year);



    // Recent records  used in dashboard or recent-activity
    @Query("SELECT r FROM FinancialRecord r WHERE r.isDeleted = false ORDER BY r.date DESC")
    List<FinancialRecord> findRecentRecords(Pageable pageable);
}