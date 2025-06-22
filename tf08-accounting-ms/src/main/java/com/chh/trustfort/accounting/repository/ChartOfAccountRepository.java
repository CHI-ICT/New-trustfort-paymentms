package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.ExpenseType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {
//    Optional<ChartOfAccount> findByCode(String code);
//    boolean existsByCode(String code);
//    Optional<ChartOfAccount> findByExpenseType(ExpenseType expenseType);
//
//}