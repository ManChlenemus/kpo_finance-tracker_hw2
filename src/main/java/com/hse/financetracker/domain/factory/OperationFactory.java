package com.hse.financetracker.domain.factory;

import com.hse.financetracker.domain.model.BankAccount;
import com.hse.financetracker.domain.model.Category;
import com.hse.financetracker.domain.model.CategoryType;
import com.hse.financetracker.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class OperationFactory {
    public Operation createOperation(
            CategoryType type,
            BigDecimal amount,
            LocalDate date,
            String description,
            BankAccount bankAccount,
            Category category
    ) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма операции должна быть положительной.");
        }

        if (bankAccount == null) {
            throw new IllegalArgumentException("Для операции должен быть указан счет.");
        }

        if (category == null) {
            throw new IllegalArgumentException("Для операции должна быть указана категория.");
        }

        return new Operation(type, amount, date, description, bankAccount, category);
    }
}