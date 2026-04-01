package com.finance.dashboard.entity;

import com.finance.dashboard.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "financial_records",
        indexes = {
                @Index(name = "idx_record_type",     columnList = "type"),
                @Index(name = "idx_record_category", columnList = "category"),
                @Index(name = "idx_record_date",     columnList = "date"),
                @Index(name = "idx_record_deleted",  columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private TransactionType type;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // Many records belong to one user (who has created it)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "created_by",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_record_user")
    )
    private User createdBy;
}