package com.hse.financetracker.application.dto;

import com.hse.financetracker.domain.model.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateOperationRequest(
        UUID bankAccountId,
        UUID categoryId,
        BigDecimal amount,
        CategoryType type,
        LocalDate date,
        String description
) {}