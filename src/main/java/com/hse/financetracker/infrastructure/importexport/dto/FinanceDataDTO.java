package com.hse.financetracker.infrastructure.importexport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FinanceDataDTO(
        List<AccountData> accounts,
        List<CategoryData> categories,
        List<OperationData> operations
) {
    public record AccountData(String id, String name, BigDecimal balance) {}
    public record CategoryData(String id, String name, String type) {}
    public record OperationData(String id, String type, BigDecimal amount, LocalDate date, String description, String accountId, String categoryId) {}
}