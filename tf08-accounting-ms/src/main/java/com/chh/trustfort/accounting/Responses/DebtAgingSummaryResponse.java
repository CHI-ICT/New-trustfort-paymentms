package com.chh.trustfort.accounting.Responses;

import com.chh.trustfort.accounting.dto.DebtAgingSummaryRow;
import lombok.Data;

import java.util.List;

@Data
public class DebtAgingSummaryResponse {
    private String responseCode;
    private String responseMessage;
    private List<DebtAgingSummaryRow> rows;
}
