package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.AccountType;
import com.chh.trustfort.accounting.enums.TaxType;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaxJournalService {

    private final JournalEntryRepository journalEntryRepository;

    /**
     * Post a tax transaction to the journal.
     * Debit Tax Expense, Credit Tax Payable.
     */
    public void postTax(TaxType taxType, BigDecimal taxAmount, LocalDate postingDate) {
        // Debit Entry - Expense
        JournalEntry debitEntry = new JournalEntry();
        debitEntry.setAmount(taxAmount);
        debitEntry.setTransactionType(TransactionType.DEBIT);
        debitEntry.setTransactionDate(postingDate);
        debitEntry.setAccount(createExpenseChartOfAccount(taxType));
        journalEntryRepository.save(debitEntry);

        // Credit Entry - Liability (using INCOME AccountType for now since no LIABILITY type exists)
        JournalEntry creditEntry = new JournalEntry();
        creditEntry.setAmount(taxAmount);
        creditEntry.setTransactionType(TransactionType.CREDIT);
        creditEntry.setTransactionDate(postingDate);
        creditEntry.setAccount(createLiabilityChartOfAccount(taxType));
        journalEntryRepository.save(creditEntry);
    }

    /**
     * Create a dummy Tax Expense ChartOfAccount.
     */
    private ChartOfAccount createExpenseChartOfAccount(TaxType taxType) {
        ChartOfAccount account = new ChartOfAccount();
        account.setCode("TAX-EXP-" + taxType.name());
        account.setName(taxType.name() + " EXPENSE");
//        account.setClassification(AccountClassification.EXPENSE);
//        account.setAccountType(AccountType.EXPENSE);
        return account;
    }

    /**
     * Create a dummy Tax Payable ChartOfAccount.
     */
    private ChartOfAccount createLiabilityChartOfAccount(TaxType taxType) {
        ChartOfAccount account = new ChartOfAccount();
        account.setCode("TAX-PAY-" + taxType.name());
        account.setName(taxType.name() + " PAYABLE");
//        account.setClassification(AccountClassification.LIABILITY);
//        account.setAccountType(AccountType.INCOME); // Using INCOME because you have only INCOME/EXPENSE in your AccountType enum
        return account;
    }
}