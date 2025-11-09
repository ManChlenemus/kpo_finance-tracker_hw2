package com.hse.financetracker.domain.visitor;

import com.hse.financetracker.domain.model.BankAccount;
import com.hse.financetracker.domain.model.Category;
import com.hse.financetracker.domain.model.Operation;

public interface DataVisitor {
    void visit(BankAccount account);
    void visit(Category category);
    void visit(Operation operation);
}