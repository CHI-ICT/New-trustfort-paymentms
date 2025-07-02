package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TaxType;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxReconciliationService {

    private final JournalEntryRepository journalEntryRepository;
    private final TaxFilingReportService taxFilingReportService;
    private final AesService aesService;
    private final Gson gson;

    public String reconcileTaxPostingsEncrypted(LocalDate startDate, LocalDate endDate, AppUser user) {
        List<ReconciliationResultDTO> results = new ArrayList<>();

        for (TaxType taxType : TaxType.values()) {
            BigDecimal expected = taxFilingReportService.calculateTotalTaxForType(taxType, startDate, endDate);

            BigDecimal posted = journalEntryRepository.sumTaxAmountByClassificationAndAccountName(
                    AccountClassification.LIABILITY,
                    taxType.name() + " PAYABLE",
                    startDate,
                    endDate
            );

            BigDecimal discrepancy = (expected != null ? expected : BigDecimal.ZERO)
                    .subtract(posted != null ? posted : BigDecimal.ZERO);

            ReconciliationResultDTO dto = new ReconciliationResultDTO();
            dto.setTaxType(taxType);
            dto.setExpectedAmount(expected != null ? expected : BigDecimal.ZERO);
            dto.setPostedAmount(posted != null ? posted : BigDecimal.ZERO);
            dto.setDiscrepancyAmount(discrepancy);

            results.add(dto);
        }

        String response = SecureResponseUtil.success("Tax reconciliation completed", results);
        return aesService.encrypt(response, user);
    }
}
