package com.hse.financetracker.infrastructure.importexport;

import com.hse.financetracker.domain.model.BankAccount;
import com.hse.financetracker.domain.model.Category;
import com.hse.financetracker.domain.model.CategoryType;
import com.hse.financetracker.domain.model.Operation;
import com.hse.financetracker.domain.repository.BankAccountRepository;
import com.hse.financetracker.domain.repository.CategoryRepository;
import com.hse.financetracker.domain.repository.OperationRepository;
import com.hse.financetracker.infrastructure.importexport.dto.FinanceDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractDataImporter {

    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    @Transactional
    public final void importFromFile(String filePath) throws IOException {
        String content = readFileContent(filePath);
        FinanceDataDTO data = parseData(content);
        saveData(data);
    }

    private String readFileContent(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    protected abstract FinanceDataDTO parseData(String content) throws IOException;

    private void saveData(FinanceDataDTO data) {
        operationRepository.deleteAll();
        bankAccountRepository.deleteAll();
        categoryRepository.deleteAll();

        data.accounts().forEach(accData -> {
            BankAccount account = new BankAccount(accData.name(), accData.balance());
            account.setId(UUID.fromString(accData.id()));
            bankAccountRepository.save(account);
        });

        data.categories().forEach(catData -> {
            Category category = new Category(catData.name(), CategoryType.valueOf(catData.type()));
            category.setId(UUID.fromString(catData.id()));
            categoryRepository.save(category);
        });

        data.operations().forEach(opData -> {
            BankAccount account = bankAccountRepository.findById(UUID.fromString(opData.accountId())).orElseThrow();
            Category category = categoryRepository.findById(UUID.fromString(opData.categoryId())).orElseThrow();

            Operation operation = new Operation(CategoryType.valueOf(opData.type()), opData.amount(), opData.date(), opData.description(), account, category);
            operation.setId(UUID.fromString(opData.id()));
            operationRepository.save(operation);
        });
    }
}