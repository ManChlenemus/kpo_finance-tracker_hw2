package com.hse.financetracker.presentation.cli;

import com.hse.financetracker.application.command.CreateAccountCommand;
import com.hse.financetracker.application.command.ListAccountsCommand;
import com.hse.financetracker.application.dto.AnalyticsReport;
import com.hse.financetracker.application.dto.CreateBankAccountRequest;
import com.hse.financetracker.application.dto.CreateCategoryRequest;
import com.hse.financetracker.application.dto.CreateOperationRequest;
import com.hse.financetracker.application.service.AnalyticsFacade;
import com.hse.financetracker.application.service.DataManagementFacade;
import com.hse.financetracker.application.service.FinanceFacade;
import com.hse.financetracker.application.service.CommandExecutor;
import com.hse.financetracker.domain.model.BankAccount;
import com.hse.financetracker.domain.model.Category;
import com.hse.financetracker.domain.model.CategoryType;
import com.hse.financetracker.domain.model.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class FinanceCommands {

    private final FinanceFacade financeFacade;
    private final DataManagementFacade dataManagementFacade;
    private final CommandExecutor commandExecutor;
    private final AnalyticsFacade analyticsFacade;


    // --- Команды для создания ---

    @ShellMethod(key = "create-account", value = "Создать новый банковский счет...")
    public String createBankAccount(
            @ShellOption(help = "Название счета") String name,
            @ShellOption(help = "Начальный баланс") BigDecimal balance
    ) {
        CreateBankAccountRequest request = new CreateBankAccountRequest(name, balance);
        CreateAccountCommand command = new CreateAccountCommand(financeFacade, request);
        return commandExecutor.execute(command);
    }

    @ShellMethod(key = "create-category", value = "Создать новую категорию. Пример: create-category --name Зарплата --type INCOME")
    public String createCategory(
            @ShellOption(help = "Название категории") String name,
            @ShellOption(help = "Тип категории (INCOME или EXPENSE)") CategoryType type
    ) {
        CreateCategoryRequest request = new CreateCategoryRequest(name, type);
        Category category = financeFacade.createCategory(request);
        return "Категория успешно создана! ID: " + category.getId();
    }

    @ShellMethod(key = "add-operation", value = "Добавить новую операцию. Пример: add-operation --account-id <ID> --category-id <ID> --amount 500 --type EXPENSE")
    public String addOperation(
            @ShellOption(value = "--account-id", help = "ID счета") UUID bankAccountId,
            @ShellOption(value = "--category-id", help = "ID категории") UUID categoryId,
            @ShellOption(help = "Сумма операции") BigDecimal amount,
            @ShellOption(help = "Тип операции (INCOME или EXPENSE)") CategoryType type,
            @ShellOption(defaultValue = ShellOption.NULL, help = "Дата операции в формате ГГГГ-ММ-ДД (по умолч. - сегодня)") LocalDate date,
            @ShellOption(defaultValue = "", help = "Описание операции") String description
    ) {
        if (date == null) {
            date = LocalDate.now();
        }

        try {
            CreateOperationRequest request = new CreateOperationRequest(bankAccountId, categoryId, amount, type, date, description);
            Operation operation = financeFacade.createOperation(request);
            return "Операция успешно добавлена! ID: " + operation.getId();
        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage(); // Ловим ошибки из фасада и красиво выводим
        }
    }

    // --- Команды для просмотра ---

    @ShellMethod(key = "list-accounts", value = "Показать все банковские счета")
    public String listAccounts() {
        ListAccountsCommand command = new ListAccountsCommand(financeFacade);
        return commandExecutor.execute(command);
    }

    @ShellMethod(key = "list-categories", value = "Показать все категории")
    public String listCategories() {
        List<Category> categories = financeFacade.getAllCategories();
        if (categories.isEmpty()) {
            return "Категории не найдены.";
        }
        return "--- Ваши категории ---\n" +
                categories.stream()
                        .map(cat -> String.format("ID: %s | Название: %s | Тип: %s", cat.getId(), cat.getName(), cat.getType()))
                        .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "list-operations", value = "Показать все операции")
    public String listOperations() {
        List<Operation> operations = financeFacade.getAllOperations();
        if (operations.isEmpty()) {
            return "Операции не найдены.";
        }
        return "--- Ваши операции ---\n" +
                operations.stream()
                        .map(op -> String.format("ID: %s | %s | %s | %.2f | Счет: %s",
                                op.getId(), op.getDate(), op.getCategory().getName(), op.getAmount(), op.getBankAccount().getName()))
                        .collect(Collectors.joining("\n"));
    }

    // --- CRUD ---

    @ShellMethod(key = "delete-account", value = "Удалить банковский счет и все связанные с ним операции.")
    public String deleteAccount(@ShellOption(help = "ID счета для удаления") UUID id) {
        try {
            financeFacade.deleteBankAccount(id);
            return "Счет с ID " + id + " успешно удален.";
        } catch (Exception e) {
            return "Ошибка при удалении счета: " + e.getMessage();
        }
    }

    @ShellMethod(key = "update-category", value = "Обновить название и/или тип существующей категории.")
    public String updateCategory(
            @ShellOption(help = "ID категории для обновления") UUID id,
            @ShellOption(help = "Новое название категории") String name,
            @ShellOption(help = "Новый тип категории (INCOME или EXPENSE)") CategoryType type
    ) {
        try {
            Category updatedCategory = financeFacade.updateCategory(id, name, type);
            return String.format("Категория обновлена! ID: %s | Новое имя: %s | Новый тип: %s",
                    updatedCategory.getId(), updatedCategory.getName(), updatedCategory.getType());
        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    // --- Импорт ---

    @ShellMethod(key = "import-data", value = "Импортировать все данные из файла (JSON или YAML). ВНИМАНИЕ: текущие данные будут стерты!")
    public String importDataFromFile(@ShellOption(help = "Полный путь к файлу") String path) {
        return dataManagementFacade.importData(path);
    }

    // --- Экспорт ---

    @ShellMethod(key = "export-data", value = "Экспортировать все данные в заданном формате (например, json).")
    public String exportData(@ShellOption(defaultValue = "json") String format) {
        String result = dataManagementFacade.exportData(format);
        System.out.println(result);
        return "Экспорт завершен.";
    }

    // --- Аналитика ---

    @ShellMethod(key = "run-analytics", value = "Показать аналитический отчет за период.")
    public String runAnalytics(
            @ShellOption(value = "--start-date", help = "Дата начала (ГГГГ-ММ-ДД), по умолч. - начало текущего месяца", defaultValue = ShellOption.NULL) LocalDate startDate,
            @ShellOption(value = "--end-date", help = "Дата конца (ГГГГ-ММ-ДД), по умолч. - конец текущего месяца", defaultValue = ShellOption.NULL) LocalDate endDate
    ) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        AnalyticsReport report = analyticsFacade.generateReport(startDate, endDate);

        StringBuilder result = new StringBuilder();
        result.append(String.format("--- Аналитический отчет за период с %s по %s ---\n", report.startDate(), report.endDate()));
        result.append(String.format("Общий доход:  +%.2f\n", report.totalIncome()));
        result.append(String.format("Общий расход: -%.2f\n", report.totalExpenses()));
        result.append("--------------------------------------------------\n");
        result.append(String.format("Итог: %.2f\n\n", report.netResult()));

        if (!report.summaryByCategory().isEmpty()) {
            result.append("--- Сводка по категориям ---\n");
            report.summaryByCategory().forEach(summary -> {
                result.append(String.format("[%s] %s: %.2f\n",
                        summary.categoryType(),
                        summary.categoryName(),
                        summary.totalAmount()));
            });
        } else {
            result.append("Операций за указанный период не найдено.\n");
        }
        return result.toString();
    }
}