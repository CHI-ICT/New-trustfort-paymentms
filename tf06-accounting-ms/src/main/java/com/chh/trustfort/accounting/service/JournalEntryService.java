package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.JournalEntryRequest;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.ChartOfAccountRepository;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

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

        // ✅ THIS is the key fix — required for DB insert
        entry.setTransactionDate(
                request.getTransactionDate() != null
                        ? request.getTransactionDate().toLocalDate()
                        : LocalDate.now()
        );

        journalEntryRepository.save(entry);
    }

//    public void recordDisbursement(Long userId, Long creditLineId, BigDecimal amount) {
//        String reference = "DISB-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
//
//        JournalEntry debit = new JournalEntry();
//        debit.setUserId(userId);
//        debit.setCreditLineId(creditLineId);
//        debit.setAccountCode("CREDIT_FUND_POOL");
//        debit.setType("CREDIT");
//        debit.setAmount(amount);
//        debit.setReference(reference);
//        debit.setTimestamp(LocalDateTime.now());
//
//        JournalEntry credit = new JournalEntry();
//        credit.setUserId(userId);
//        credit.setCreditLineId(creditLineId);
//        credit.setAccountCode("WALLET");
//        credit.setType("DEBIT");
//        credit.setAmount(amount);
//        credit.setReference(reference);
//        credit.setTimestamp(LocalDateTime.now());
//
//        journalRepo.saveAll(List.of(debit, credit));
//    }

}
