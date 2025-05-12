package com.chh.trustfort.accounting.Validator;

import com.chh.trustfort.accounting.dto.TaxFilingSummaryDTO;

import java.math.BigDecimal;
import java.util.List;

public class ComplianceValidator {

    /**
     * Check if all required tax fields are populated and logical.
     */
    public static boolean validateTaxSummaries(List<TaxFilingSummaryDTO> taxSummaries) {
        for (TaxFilingSummaryDTO dto : taxSummaries) {
            if (dto.getTaxType() == null || dto.getTotalTaxAmount() == null) {
                return false;
            }

            if (dto.getTotalTaxAmount().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }
        return true;
    }
}
