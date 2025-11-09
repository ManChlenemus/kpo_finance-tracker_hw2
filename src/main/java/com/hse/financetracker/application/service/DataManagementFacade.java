package com.hse.financetracker.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hse.financetracker.domain.model.BankAccount;
import com.hse.financetracker.domain.model.Category;
import com.hse.financetracker.domain.model.Operation;
import com.hse.financetracker.domain.repository.BankAccountRepository;
import com.hse.financetracker.domain.repository.CategoryRepository;
import com.hse.financetracker.domain.repository.OperationRepository;
import com.hse.financetracker.infrastructure.importexport.AbstractDataImporter;
import com.hse.financetracker.infrastructure.visitor.JsonDataVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataManagementFacade {

    private final ApplicationContext context;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;


    public String exportData(String format) {
        if (!"json".equalsIgnoreCase(format)) {
            return "Ошибка: Экспорт поддерживается только в формате JSON.";
        }

        try {
            JsonDataVisitor visitor = new JsonDataVisitor();
            List<BankAccount> accounts = bankAccountRepository.findAll();
            List<Category> categories = categoryRepository.findAll();
            List<Operation> operations = operationRepository.findAll();

            accounts.forEach(acc -> acc.accept(visitor));
            categories.forEach(cat -> cat.accept(visitor));
            operations.forEach(op -> op.accept(visitor));

            return visitor.getJsonOutput();
        } catch (JsonProcessingException e) {
            return "Ошибка при формировании JSON: " + e.getMessage();
        }
    }

    public String importData(String filePath) {
        try {
            AbstractDataImporter importer = getImporter(filePath);
            importer.importFromFile(filePath);
            return "Данные успешно импортированы из " + filePath;
        } catch (IOException e) {
            return "Ошибка импорта: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    private AbstractDataImporter getImporter(String filePath) {
        if (filePath.endsWith(".json")) {
            return context.getBean("jsonImporter", AbstractDataImporter.class);
        } else if (filePath.endsWith(".yaml") || filePath.endsWith(".yml")) {
            return context.getBean("yamlImporter", AbstractDataImporter.class);
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат файла: " + filePath);
        }
    }
}