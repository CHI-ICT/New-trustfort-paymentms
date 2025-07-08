package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.model.AccountingLedgerEntry;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.BankStatementRecord;
import com.chh.trustfort.accounting.repository.BankStatementRepository;
import com.chh.trustfort.accounting.repository.AccountingLedgerEntryRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.BankReconciliationService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankReconciliationServiceImpl implements BankReconciliationService {

    private final BankStatementRepository bankStatementRepository;

//    @Qualifier("accountingLedgerEntryRepository")
//    @Autowired
    private final AccountingLedgerEntryRepository accountingLedgerEntryRepository;

    private final AesService aesService;
    private final Gson gson;

    @Override
    public String reconcileBankWithLedger(LocalDate startDate, LocalDate endDate, AppUser user) {
        log.info("🔍 Starting reconciliation from {} to {} by {}", startDate, endDate, user.getEmail());

        List<BankStatementRecord> bankRecords = bankStatementRepository.findByTransactionDateBetween(startDate, endDate);
        List<AccountingLedgerEntry> ledgerEntries = accountingLedgerEntryRepository.findByTransactionDateBetween(startDate, endDate);

        List<ReconciliationResultDTO> results = new ArrayList<>();

        for (BankStatementRecord bankRecord : bankRecords) {
            Optional<AccountingLedgerEntry> match = ledgerEntries.stream()
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

        log.info("✅ Reconciliation completed with {} results", results.size());
        return aesService.encrypt(gson.toJson(results), user);
    }
}
