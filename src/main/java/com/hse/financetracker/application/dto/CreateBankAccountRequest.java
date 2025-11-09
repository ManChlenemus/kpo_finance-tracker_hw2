package com.hse.financetracker.application.dto;

import java.math.BigDecimal;

public record CreateBankAccountRequest(
        String name,
        BigDecimal initialBalance
) {}