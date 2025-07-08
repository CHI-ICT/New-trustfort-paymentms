package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.dto.DoubleEntryRequest;
import com.chh.trustfort.accounting.dto.JournalEntryRequest;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.ChartOfAccountAccountRepository;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountAccountRepository chartOfAccountRepository;
    private final AesService aesService;
    private final Gson gson;

    public String createdJournalEntry(JournalEntryRequest request, AppUser user) {
        ChartOfAccount account = chartOfAccountRepository.findByAccountCode(request.getAccountCode())
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

        // Plain response map
        String plainResponse = gson.toJson(SecureResponseUtil.success("Journal entry recorded successfully.", Map.of(
                "entryId", entry.getId(),
                "account", entry.getAccount().getAccountCode(),
                "classification", entry.getAccount().getClassification(),
                "amount", entry.getAmount(),
                "transactionType", entry.getTransactionType(),
                "date", entry.getTransactionDate().toString(),
                "businessUnit", entry.getBusinessUnit()
        )));

        // 🔐 If AppUser is present (i.e., called via secure controller), encrypt the response
        if (user != null && user.getEcred() != null) {
            return aesService.encrypt(plainResponse, user);
        }

        // 🧪 For Postman/internal testing, return plain JSON response
        return plainResponse;
    }


    public String createJournalEntry(JournalEntryRequest request, AppUser user) {
        ChartOfAccount account = chartOfAccountRepository.findByAccountCode(request.getAccountCode())
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

        return aesService.encrypt(SecureResponseUtil.success("Journal entry recorded successfully.", Map.of(
                "entryId", entry.getId(),
                "account", entry.getAccount().getAccountCode(),
                "classification", entry.getAccount().getClassification(),
                "amount", entry.getAmount(),
                "transactionType", entry.getTransactionType(),
                "date", entry.getTransactionDate().toString(),
                "businessUnit", entry.getBusinessUnit()
        )), user);
    }

    public String recordDoubleEntry(DoubleEntryRequest request, AppUser user) {
        ChartOfAccount debitAccount = chartOfAccountRepository.findByAccountCode(request.getDebitAccountCode())
                .orElseThrow(() -> new RuntimeException("Debit account not found: " + request.getDebitAccountCode()));

        ChartOfAccount creditAccount = chartOfAccountRepository.findByAccountCode(request.getCreditAccountCode())
                .orElseThrow(() -> new RuntimeException("Credit account not found: " + request.getCreditAccountCode()));

        LocalDate entryDate = request.getTransactionDate() != null
                ? request.getTransactionDate().toLocalDate()
                : LocalDate.now();

        JournalEntry debitEntry = new JournalEntry();
        debitEntry.setAccount(debitAccount);
        debitEntry.setTransactionType(TransactionType.DEBIT);
        debitEntry.setAmount(request.getAmount());
        debitEntry.setDescription(request.getDescription());
        debitEntry.setReference(request.getReference());
        debitEntry.setDepartment(request.getDepartment());
        debitEntry.setBusinessUnit(request.getBusinessUnit());
        debitEntry.setTransactionDate(entryDate);

        JournalEntry creditEntry = new JournalEntry();
        creditEntry.setAccount(creditAccount);
        creditEntry.setTransactionType(TransactionType.CREDIT);
        creditEntry.setAmount(request.getAmount());
        creditEntry.setDescription(request.getDescription());
        creditEntry.setReference(request.getReference());
        creditEntry.setDepartment(request.getDepartment());
        creditEntry.setBusinessUnit(request.getBusinessUnit());
        creditEntry.setTransactionDate(entryDate);

        journalEntryRepository.save(debitEntry);
        journalEntryRepository.save(creditEntry);

        log.info("📘 Double entry posted: DR [{}] → CR [{}] | Amount: {} | Ref: {}",
                request.getDebitAccountCode(), request.getCreditAccountCode(), request.getAmount(), request.getReference());

        return aesService.encrypt(SecureResponseUtil.success("Double-entry journal successfully recorded.", Map.of(
                "reference", request.getReference(),
                "amount", request.getAmount(),
                "debitAccount", debitAccount.getAccountName(),
                "creditAccount", creditAccount.getAccountName(),
                "date", entryDate.toString()
        )), user);
    }
}