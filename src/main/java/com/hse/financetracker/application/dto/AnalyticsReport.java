package com.hse.financetracker.application.dto;

import com.hse.financetracker.domain.model.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record AnalyticsReport(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netResult,
        List<CategorySummary> summaryByCategory
) {
    public record CategorySummary(String categoryName, CategoryType categoryType, BigDecimal totalAmount) {}
}