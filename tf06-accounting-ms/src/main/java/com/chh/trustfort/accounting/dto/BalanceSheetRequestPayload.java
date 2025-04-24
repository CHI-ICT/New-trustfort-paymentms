package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BalanceSheetRequestPayload {
    private LocalDateTime asOfDate;
}
