package com.hse.financetracker.application.command;

import com.hse.financetracker.application.dto.CreateBankAccountRequest;
import com.hse.financetracker.application.service.FinanceFacade;
import com.hse.financetracker.domain.model.BankAccount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAccountCommand implements Command<String> {

    private final FinanceFacade financeFacade;
    private final CreateBankAccountRequest request;

    @Override
    public String execute() {
        BankAccount account = financeFacade.createBankAccount(request);
        return "Счет успешно создан! ID: " + account.getId();
    }
}