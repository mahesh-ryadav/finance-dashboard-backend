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


    @Query("SELECT r FROM FinancialRecord r WHERE r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findAllActive(Pageable pageable);

    @Query("SELECT r FROM FinancialRecord r WHERE r.id = :id AND r.isDeleted = false")
    Optional<FinancialRecord> findByIdAndIsDeletedFalse(@Param("id") Long id);


    @Query("SELECT r FROM FinancialRecord r WHERE r.type = :type AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByType(
            @Param("type") TransactionType type,
            Pageable pageable);

    @Query("SELECT r FROM FinancialRecord r WHERE LOWER(r.category) = LOWER(:category) AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByCategory(
            @Param("category") String category,
            Pageable pageable);

    @Query("SELECT r FROM FinancialRecord r WHERE r.date BETWEEN :startDate AND :endDate AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT r FROM FinancialRecord r WHERE r.type = :type AND LOWER(r.category) = LOWER(:category) AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByTypeAndCategory(
            @Param("type") TransactionType type,
            @Param("category") String category,
            Pageable pageable);

    @Query("SELECT r FROM FinancialRecord r WHERE r.type = :type AND r.date BETWEEN :startDate AND :endDate AND r.isDeleted = false ORDER BY r.date DESC")
    Page<FinancialRecord> findByTypeAndDateRange(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);


    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type AND r.isDeleted = false")
    BigDecimal sumAmountByType(@Param("type") TransactionType type);



    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.isDeleted = false GROUP BY r.category ORDER BY SUM(r.amount) DESC")
    List<Object[]> findCategorySummary();



    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type AND r.isDeleted = false GROUP BY r.category ORDER BY SUM(r.amount) DESC")
    List<Object[]> findCategorySummaryByType(@Param("type") TransactionType type);



    @Query("SELECT MONTH(r.date), r.type, SUM(r.amount) FROM FinancialRecord r WHERE YEAR(r.date) = :year AND r.isDeleted = false GROUP BY MONTH(r.date), r.type ORDER BY MONTH(r.date)")
    List<Object[]> findMonthlyTrends(@Param("year") int year);


    @Query("SELECT r FROM FinancialRecord r WHERE r.isDeleted = false ORDER BY r.date DESC")
    List<FinancialRecord> findRecentRecords(Pageable pageable);
}