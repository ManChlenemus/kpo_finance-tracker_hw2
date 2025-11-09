package com.hse.financetracker.domain.model;

import com.hse.financetracker.domain.visitor.DataVisitor;

public interface Visitable {
    void accept(DataVisitor visitor);
}