package com.hse.financetracker.application.dto;

import com.hse.financetracker.domain.model.CategoryType;

public record CreateCategoryRequest(
        String name,
        CategoryType type
) {}