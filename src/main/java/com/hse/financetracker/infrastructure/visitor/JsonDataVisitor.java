package com.hse.financetracker.infrastructure.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hse.financetracker.domain.model.BankAccount;
import com.hse.financetracker.domain.model.Category;
import com.hse.financetracker.domain.model.Operation;
import com.hse.financetracker.domain.visitor.DataVisitor;
import com.hse.financetracker.infrastructure.importexport.dto.FinanceDataDTO;

import java.util.ArrayList;
import java.util.List;

public class JsonDataVisitor implements DataVisitor {

    private final List<FinanceDataDTO.AccountData> accounts = new ArrayList<>();
    private final List<FinanceDataDTO.CategoryData> categories = new ArrayList<>();
    private final List<FinanceDataDTO.OperationData> operations = new ArrayList<>();

    @Override
    public void visit(BankAccount account) {
        accounts.add(new FinanceDataDTO.AccountData(
                account.getId().toString(),
                account.getName(),
                account.getBalance()
        ));
    }

    @Override
    public void visit(Category category) {
        categories.add(new FinanceDataDTO.CategoryData(
                category.getId().toString(),
                category.getName(),
                category.getType().name()
        ));
    }

    @Override
    public void visit(Operation operation) {
        operations.add(new FinanceDataDTO.OperationData(
                operation.getId().toString(),
                operation.getType().name(),
                operation.getAmount(),
                operation.getDate(),
                operation.getDescription(),
                operation.getBankAccount().getId().toString(),
                operation.getCategory().getId().toString()
        ));
    }

    public String getJsonOutput() throws JsonProcessingException {
        FinanceDataDTO finalData = new FinanceDataDTO(accounts, categories, operations);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalData);
    }
}