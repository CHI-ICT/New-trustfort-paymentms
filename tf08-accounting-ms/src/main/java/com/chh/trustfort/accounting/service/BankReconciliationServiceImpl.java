package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.BankStatementRecord;
import com.chh.trustfort.accounting.model.LedgerEntry;
import com.chh.trustfort.accounting.repository.BankStatementRepository;
import com.chh.trustfort.accounting.repository.LedgerEntryRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankReconciliationServiceImpl implements BankReconciliationService {

    private final BankStatementRepository bankStatementRepository;

    @Autowired
    @Qualifier("accountingLedgerEntryRepository")
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String reconcileBankWithLedger(LocalDate startDate, LocalDate endDate, AppUser user) {
        List<BankStatementRecord> bankRecords = bankStatementRepository.findByTransactionDateBetween(startDate, endDate);
        List<LedgerEntry> ledgerEntries = ledgerEntryRepository.findByTransactionDateBetween(startDate, endDate);

        List<ReconciliationResultDTO> results = new ArrayList<>();

        for (BankStatementRecord bankRecord : bankRecords) {
            Optional<LedgerEntry> match = ledgerEntries.stream()
                    .filter(entry -> entry.getAmount().compareTo(bankRecord.getAmount()) == 0 &&
                            entry.getTransactionDate().equals(bankRecord.getTransactionDate()))
                    .findFirst();

            ReconciliationResultDTO result = new ReconciliationResultDTO();
            result.setBankReference(bankRecord.getBankReference());
            result.setAmount(bankRecord.getAmount());
            result.setDate(bankRecord.getTransactionDate());

            if (match.isPresent()) {
                bankRecord.setStatus("MATCHED");
                bankRecord.setInternalReference(match.get().getReference());
                result.setMatchStatus("MATCHED");
            } else {
                bankRecord.setStatus("UNMATCHED");
                result.setMatchStatus("UNMATCHED");
            }

            bankStatementRepository.save(bankRecord);
            results.add(result);
        }

        return aesService.encrypt(gson.toJson(results), user);
    }
}
