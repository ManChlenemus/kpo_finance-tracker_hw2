package com.hse.financetracker.infrastructure.importexport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hse.financetracker.domain.repository.BankAccountRepository;
import com.hse.financetracker.domain.repository.CategoryRepository;
import com.hse.financetracker.domain.repository.OperationRepository;
import com.hse.financetracker.infrastructure.importexport.dto.FinanceDataDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("yamlImporter")
public class YamlDataImporter extends AbstractDataImporter {

    private final ObjectMapper objectMapper;

    public YamlDataImporter(BankAccountRepository bankAccountRepository, CategoryRepository categoryRepository, OperationRepository operationRepository) {
        super(bankAccountRepository, categoryRepository, operationRepository);
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected FinanceDataDTO parseData(String content) throws IOException {
        return objectMapper.readValue(content, FinanceDataDTO.class);
    }
}