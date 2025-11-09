package com.hse.financetracker.domain.model;

import com.hse.financetracker.domain.visitor.DataVisitor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class BankAccount implements Visitable {

    @Id
    private UUID id;
    private String name;
    private BigDecimal balance;

    public BankAccount(String name, BigDecimal balance) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.balance = balance;
    }

    @Override
    public void accept(DataVisitor visitor) {
        visitor.visit(this);
    }
}