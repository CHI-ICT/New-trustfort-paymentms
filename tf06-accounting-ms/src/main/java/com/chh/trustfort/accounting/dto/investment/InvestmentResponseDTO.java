package com.chh.trustfort.accounting.dto.investment;

import com.chh.trustfort.accounting.enums.InvestmentSubtype;
import com.chh.trustfort.accounting.enums.InvestmentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentResponseDTO {
    private Long id;
    private String reference;
    private BigDecimal amount;
    private String currency;
    private InvestmentType type;
    private InvestmentSubtype subtype;
    private boolean isParticipating;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private BigDecimal roi;
    private String institutionName;
    private String status;
    private BigDecimal expectedReturn;
}

