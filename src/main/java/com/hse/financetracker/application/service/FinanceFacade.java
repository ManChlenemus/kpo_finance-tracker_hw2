package com.hse.financetracker.application.service;

import com.hse.financetracker.application.dto.CreateBankAccountRequest;
import com.hse.financetracker.application.dto.CreateCategoryRequest;
import com.hse.financetracker.application.dto.CreateOperationRequest;
import com.hse.financetracker.domain.factory.OperationFactory;
import com.hse.financetracker.domain.model.BankAccount;
import com.hse.financetracker.domain.model.Category;
import com.hse.financetracker.domain.model.CategoryType;
import com.hse.financetracker.domain.model.Operation;
import com.hse.financetracker.domain.repository.BankAccountRepository;
import com.hse.financetracker.domain.repository.CategoryRepository;
import com.hse.financetracker.domain.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceFacade {

    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;
    private final OperationFactory operationFactory;

    @Transactional
    public BankAccount createBankAccount(CreateBankAccountRequest request) {
        BankAccount newAccount = new BankAccount(request.name(), request.initialBalance());
        return bankAccountRepository.save(newAccount);
    }

    @Transactional
    public Category createCategory(CreateCategoryRequest request) {
        Category newCategory = new Category(request.name(), request.type());
        return categoryRepository.save(newCategory);
    }

    @Transactional
    public Operation createOperation(CreateOperationRequest request) {
        BankAccount account = bankAccountRepository.findById(request.bankAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (request.type() == CategoryType.INCOME) {
            account.setBalance(account.getBalance().add(request.amount()));
        } else {
            account.setBalance(account.getBalance().subtract(request.amount()));
        }

        bankAccountRepository.save(account);

        Operation newOperation = operationFactory.createOperation(
                request.type(),
                request.amount(),
                request.date(),
                request.description(),
                account,
                category
        );
        return operationRepository.save(newOperation);
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    @Transactional
    public void deleteBankAccount(UUID accountId) {
        List<Operation> operations = operationRepository.findAllByBankAccountId(accountId);
        operationRepository.deleteAll(operations);
        bankAccountRepository.deleteById(accountId);
    }

    @Transactional
    public Category updateCategory(UUID categoryId, String newName, CategoryType newType) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Категория с ID " + categoryId + " не найдена."));

        category.setName(newName);
        category.setType(newType);
        return categoryRepository.save(category);
    }
}