package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.TaxFilingSummaryDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TaxType;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxFilingReportService {

    private final JournalEntryRepository journalEntryRepository;

    /**
     * Generate tax filing summaries for a given period.
     */
    public List<TaxFilingSummaryDTO> generateTaxFilingReport(LocalDate startDate, LocalDate endDate) {
        List<TaxFilingSummaryDTO> summaries = new ArrayList<>();

        for (TaxType taxType : TaxType.values()) {
            BigDecimal total = journalEntryRepository.sumTaxAmountByClassificationAndAccountName(
                    AccountClassification.LIABILITY,
                    taxType.name() + " PAYABLE",
                    startDate,
                    endDate
            );

            TaxFilingSummaryDTO dto = new TaxFilingSummaryDTO();
            dto.setTaxType(taxType);
            dto.setTotalTaxAmount(total != null ? total : BigDecimal.ZERO);

            summaries.add(dto);
        }

        return summaries;
    }

    public BigDecimal calculateTotalTaxForType(TaxType taxType, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = journalEntryRepository.sumTaxAmountByClassificationAndAccountName(
                AccountClassification.LIABILITY,
                taxType.name() + " PAYABLE",
                startDate,
                endDate
        );
        return total != null ? total : BigDecimal.ZERO;
    }
}
