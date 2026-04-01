package com.finance.dashboard.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendResponse {

    private int month;           // 1 = "January"  2 = "February" and so on
    private String monthName;    // "January", "February"  and so in
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;      // income - expense
}