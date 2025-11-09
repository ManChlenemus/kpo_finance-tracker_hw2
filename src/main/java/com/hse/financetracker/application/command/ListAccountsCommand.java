package com.hse.financetracker.application.command;

import com.hse.financetracker.application.service.FinanceFacade;
import com.hse.financetracker.domain.model.BankAccount;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ListAccountsCommand implements Command<String> {

    private final FinanceFacade financeFacade;

    @Override
    public String execute() {
        List<BankAccount> accounts = financeFacade.getAllBankAccounts();
        if (accounts.isEmpty()) {
            return "Счета не найдены.";
        }
        return "--- Ваши счета ---\n" +
                accounts.stream()
                        .map(acc -> String.format("ID: %s | Название: %s | Баланс: %.2f", acc.getId(), acc.getName(), acc.getBalance()))
                        .collect(Collectors.joining("\n"));
    }
}