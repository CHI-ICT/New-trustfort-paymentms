package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.JournalEntryRequest;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.ChartOfAccountRepository;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

    /**
     * Create a single journal entry (used mostly for test/manual postings).
     */
    public void createJournalEntry(JournalEntryRequest request) {
        ChartOfAccount account = chartOfAccountRepository.findByCode(request.getAccountCode())
                .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountCode()));

        JournalEntry entry = new JournalEntry();
        entry.setAccount(account);
        entry.setAmount(request.getAmount());
        entry.setTransactionType(request.getTransactionType());
        entry.setDescription(request.getDescription());
        entry.setReference(request.getReference());
        entry.setDepartment(request.getDepartment());
        entry.setBusinessUnit(request.getBusinessUnit());
        entry.setTransactionDate(
                request.getTransactionDate() != null
                        ? request.getTransactionDate().toLocalDate()
                        : LocalDate.now()
        );

        journalEntryRepository.save(entry);

        log.info("✅ Single Journal Entry created: [{}] - {} {}",
                request.getTransactionType(), request.getAccountCode(), request.getAmount());
    }

    /**
     * Record a double-entry journal (Debit and Credit entries for accounting compliance).
     */
    public void recordDoubleEntry(
            String debitAccountCode,
            String creditAccountCode,
            String reference,
            String description,
            BigDecimal amount,
            String department,
            String businessUnit,
            LocalDateTime transactionDate
    ) {
        ChartOfAccount debitAccount = chartOfAccountRepository.findByCode(debitAccountCode)
                .orElseThrow(() -> new RuntimeException("Debit account not found: " + debitAccountCode));

        ChartOfAccount creditAccount = chartOfAccountRepository.findByCode(creditAccountCode)
                .orElseThrow(() -> new RuntimeException("Credit account not found: " + creditAccountCode));

        LocalDate entryDate = transactionDate != null ? transactionDate.toLocalDate() : LocalDate.now();

        JournalEntry debitEntry = new JournalEntry();
        debitEntry.setAccount(debitAccount);
        debitEntry.setTransactionType(TransactionType.DEBIT);
        debitEntry.setAmount(amount);
        debitEntry.setDescription(description);
        debitEntry.setReference(reference);
        debitEntry.setDepartment(department);
        debitEntry.setBusinessUnit(businessUnit);
        debitEntry.setTransactionDate(entryDate);

        JournalEntry creditEntry = new JournalEntry();
        creditEntry.setAccount(creditAccount);
        creditEntry.setTransactionType(TransactionType.CREDIT);
        creditEntry.setAmount(amount);
        creditEntry.setDescription(description);
        creditEntry.setReference(reference);
        creditEntry.setDepartment(department);
        creditEntry.setBusinessUnit(businessUnit);
        creditEntry.setTransactionDate(entryDate);

        journalEntryRepository.save(debitEntry);
        journalEntryRepository.save(creditEntry);

        log.info("📘 Double entry posted: DR [{}] → CR [{}] | Amount: {} | Ref: {}",
                debitAccountCode, creditAccountCode, amount, reference);
    }
}
