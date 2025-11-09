package com.hse.financetracker.domain.model;

import com.hse.financetracker.domain.visitor.DataVisitor;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Category implements Visitable{

    @Id
    private UUID id;
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryType type;

    public Category(String name, CategoryType type) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
    }

    @Override
    public void accept(DataVisitor visitor) {
        visitor.visit(this);
    }
}