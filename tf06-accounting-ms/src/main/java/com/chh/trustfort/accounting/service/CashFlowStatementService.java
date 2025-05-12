package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CashFlowStatementDTO;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.AccountType;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashFlowStatementService {

    private final JournalEntryRepository journalEntryRepository;

    public CashFlowStatementDTO generateCashFlowStatement(StatementFilterDTO filter) {
        List<JournalEntry> entries = journalEntryRepository.findByTransactionDateBetween(filter.getStartDate(), filter.getEndDate());

        BigDecimal operating = BigDecimal.ZERO;
        BigDecimal investing = BigDecimal.ZERO;
        BigDecimal financing = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            AccountClassification classification = entry.getAccount().getClassification();
            BigDecimal amount = entry.getAmount();
            TransactionType type = entry.getTransactionType();

            // Normalize sign: credits reduce cash, debits increase cash
            BigDecimal signedAmount = type.isCredit() ? amount.negate() : amount;

            switch (classification) {
                case REVENUE:
                case EXPENSE:
                    operating = operating.add(signedAmount);
                    break;

                case FIXED_ASSET:
                case INVESTMENT:
                    investing = investing.add(signedAmount);
                    break;

                case EQUITY:
                case LOAN:
                case LIABILITY: // e.g., loans can be liabilities
                    financing = financing.add(signedAmount);
                    break;

                default:
                    break;
            }
        }

        CashFlowStatementDTO dto = new CashFlowStatementDTO();
        dto.setOperatingActivities(operating);
        dto.setInvestingActivities(investing);
        dto.setFinancingActivities(financing);
        dto.setNetCashFlow(operating.add(investing).add(financing));

        return dto;
    }
}
