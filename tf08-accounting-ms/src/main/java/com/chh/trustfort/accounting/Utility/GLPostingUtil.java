package com.chh.trustfort.accounting.Utility;

import com.chh.trustfort.accounting.enums.GLPostingType;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.AccountingLedgerEntry;
import com.chh.trustfort.accounting.service.AccountingLedgerEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class GLPostingUtil {

    private final AccountingLedgerEntryService accountingLedgerEntryService;

    public void post(String accountCode, BigDecimal amount, TransactionType type,
                     GLPostingType postingType, String reference, String description,
                     String businessUnit, String department, LocalDate txnDate) {

        AccountingLedgerEntry entry = new AccountingLedgerEntry();
        entry.setAccountCode(accountCode);
        entry.setAmount(amount);
        entry.setTransactionType(type);
        entry.setPostingType(postingType);
        entry.setDescription(description);
        entry.setReference(reference);
        entry.setBusinessUnit(businessUnit);
        entry.setDepartment(department);
        entry.setTransactionDate(txnDate);

        accountingLedgerEntryService.postToGL(entry);
    }
}
