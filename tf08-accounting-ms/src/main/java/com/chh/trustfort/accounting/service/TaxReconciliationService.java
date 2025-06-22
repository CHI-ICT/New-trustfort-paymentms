package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
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
public class TaxReconciliationService {

    private final JournalEntryRepository journalEntryRepository;
    private final TaxFilingReportService taxFilingReportService;

    public List<ReconciliationResultDTO> reconcileTaxPostings(LocalDate startDate, LocalDate endDate) {
        List<ReconciliationResultDTO> results = new ArrayList<>();

        for (TaxType taxType : TaxType.values()) {
            // 1. Expected Tax: based on tax filing report
            BigDecimal expected = taxFilingReportService.calculateTotalTaxForType(taxType, startDate, endDate);

            // 2. Posted Tax: based on journal entries
            BigDecimal posted = journalEntryRepository.sumTaxAmountByClassificationAndAccountName(
                    AccountClassification.LIABILITY,
                    taxType.name() + " PAYABLE",
                    startDate,
                    endDate
            );

            // 3. Calculate Discrepancy
            BigDecimal discrepancy = (expected.subtract(posted));

            ReconciliationResultDTO dto = new ReconciliationResultDTO();
            dto.setTaxType(taxType);
            dto.setExpectedAmount(expected != null ? expected : BigDecimal.ZERO);
            dto.setPostedAmount(posted != null ? posted : BigDecimal.ZERO);
            dto.setDiscrepancyAmount(discrepancy);

            results.add(dto);
        }

        return results;
    }
}
