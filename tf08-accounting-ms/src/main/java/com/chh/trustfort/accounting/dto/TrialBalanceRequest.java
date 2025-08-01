package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceRequest {

    private LocalDate startDate;
    private LocalDate endDate;
}
