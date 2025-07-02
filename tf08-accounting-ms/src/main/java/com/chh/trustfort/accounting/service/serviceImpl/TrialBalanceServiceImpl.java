package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.TrialBalanceResponse;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.ChartOfAccountAccountRepository;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import com.chh.trustfort.accounting.service.TrialBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrialBalanceServiceImpl implements TrialBalanceService {

    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountAccountRepository accountRepository;

    @Override
    public List<TrialBalanceResponse> generateTrialBalance(LocalDate startDate, LocalDate endDate) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.atTime(23, 59, 59);

        List<ChartOfAccount> accounts = accountRepository.findAll();

        return accounts.stream().map(account -> {
            List<JournalEntry> entries = journalEntryRepository
                    .findByAccountAndTransactionDateBetween(account, from.toLocalDate(), to.toLocalDate());

            BigDecimal totalDebit = entries.stream()
                    .filter(e -> e.getTransactionType() == TransactionType.DEBIT)
                    .map(JournalEntry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCredit = entries.stream()
                    .filter(e -> e.getTransactionType() == TransactionType.CREDIT)
                    .map(JournalEntry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal balance = totalDebit.subtract(totalCredit);

            return TrialBalanceResponse.builder()
                    .accountCode(account.getAccountCode())
                    .accountName(account.getAccountName())
                    .totalDebit(totalDebit)
                    .totalCredit(totalCredit)
                    .balance(balance)
                    .currency(account.getCurrency())
                    .classification(String.valueOf(account.getClassification()))
                    .subsidiary(String.valueOf(account.getSubsidiary()))
                    .accountStatus(String.valueOf(account.getStatus()))
                    .fullAccountCode(account.getFullAccountCode())
                    .currencyPrefixedCode(account.getCurrencyPrefixedCode())
                    .normalBalance(String.valueOf(account.getNormalBalance()))
                    .expenseType(String.valueOf(account.getExpenseType()))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReportViewerResponse> generateTrialBalanceForViewer(LocalDate startDate, LocalDate endDate) {
        List<TrialBalanceResponse> trialBalances = generateTrialBalance(startDate, endDate);

        return trialBalances.stream().map(tb -> {
            Map<String, Object> fields = new LinkedHashMap<>();
            fields.put("Account Code", tb.getAccountCode());
            fields.put("Account Name", tb.getAccountName());
            fields.put("Debit", tb.getTotalDebit());
            fields.put("Credit", tb.getTotalCredit());
            fields.put("Balance", tb.getBalance());
            fields.put("Currency", tb.getCurrency());
            fields.put("Classification", tb.getClassification());
            fields.put("Subsidiary", tb.getSubsidiary());
            fields.put("Status", tb.getAccountStatus());
            return new ReportViewerResponse(fields);
        }).collect(Collectors.toList());
    }
}

