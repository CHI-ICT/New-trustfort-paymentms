package com.chh.trustfort.accounting.dto.investment;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class AssetClassResponseDTO {
    private Long id;
    private String name;
    private BigDecimal averageReturnRate;
    private String riskLevel;
    private String regulatorCode;
}

