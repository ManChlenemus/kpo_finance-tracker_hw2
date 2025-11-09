package com.hse.financetracker.domain.model;

import com.hse.financetracker.domain.visitor.DataVisitor;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Operation implements Visitable{

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CategoryType type;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Operation(CategoryType type, BigDecimal amount, LocalDate date, String description, BankAccount bankAccount, Category category) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.bankAccount = bankAccount;
        this.category = category;
    }

    @Override
    public void accept(DataVisitor visitor) {
        visitor.visit(this);
    }
}