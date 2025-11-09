package com.hse.financetracker.domain.repository;

import com.hse.financetracker.domain.model.CategoryType;
import com.hse.financetracker.domain.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {
    List<Operation> findAllByTypeAndDateBetween(CategoryType type, LocalDate startDate, LocalDate endDate);
    List<Operation> findAllByBankAccountId(UUID bankAccountId);
    List<Operation> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}