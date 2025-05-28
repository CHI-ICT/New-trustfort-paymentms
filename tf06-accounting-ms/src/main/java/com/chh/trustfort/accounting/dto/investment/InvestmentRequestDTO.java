package com.chh.trustfort.accounting.dto.investment;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.chh.trustfort.accounting.enums.InvestmentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvestmentRequestDTO {
    private BigDecimal amount;
    private InvestmentType type;
    private Long assetClassId;
    private int tenorYears;
    private Long institutionId;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private InsuranceProductType insuranceProductType;
    private boolean isParticipating;
}
