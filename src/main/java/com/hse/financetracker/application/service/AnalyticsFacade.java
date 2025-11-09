package com.hse.financetracker.application.service;

import com.hse.financetracker.application.dto.AnalyticsReport;
import com.hse.financetracker.domain.model.CategoryType;
import com.hse.financetracker.domain.model.Operation;
import com.hse.financetracker.domain.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsFacade {

    private final OperationRepository operationRepository;

    @Transactional(readOnly = true)
    public AnalyticsReport generateReport(LocalDate startDate, LocalDate endDate) {
        List<Operation> operations = operationRepository.findAllByDateBetween(startDate, endDate);
        BigDecimal totalIncome = calculateTotalByType(operations, CategoryType.INCOME);
        BigDecimal totalExpenses = calculateTotalByType(operations, CategoryType.EXPENSE);
        BigDecimal netResult = totalIncome.subtract(totalExpenses);
        List<AnalyticsReport.CategorySummary> summaryByCategory = operations.stream()
                .collect(Collectors.groupingBy(
                        Operation::getCategory,
                        Collectors.mapping(Operation::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)) // Суммируем суммы операций в каждой группе
                ))
                .entrySet().stream()
                .map(entry -> new AnalyticsReport.CategorySummary(
                        entry.getKey().getName(),
                        entry.getKey().getType(),
                        entry.getValue()
                ))
                .toList();

        return new AnalyticsReport(startDate, endDate, totalIncome, totalExpenses, netResult, summaryByCategory);
    }

    private BigDecimal calculateTotalByType(List<Operation> operations, CategoryType type) {
        return operations.stream()
                .filter(op -> op.getType() == type)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}